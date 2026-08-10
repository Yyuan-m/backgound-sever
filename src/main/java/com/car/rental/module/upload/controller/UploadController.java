package com.car.rental.module.upload.controller;

import cn.hutool.core.util.IdUtil;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.Result;
import com.car.rental.common.util.SecurityUtil;
import com.car.rental.entity.SysFile;
import com.car.rental.entity.SysUser;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.system.service.SysFileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通用文件上传接口
 * 上传成功后自动写入 sys_file 表，返回 url + fileId 等元信息
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    private final SysFileService sysFileService;
    private final SecurityUtil securityUtil;
    private final SysUserMapper sysUserMapper;

    /** 图片扩展名白名单 */
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");
    /** 文档扩展名白名单 */
    private static final Set<String> DOC_EXTENSIONS = Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "csv");
    /** 视频扩展名白名单 */
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm");
    /** 允许的扩展名集合 */
    private static final Set<String> ALLOWED_EXTENSIONS = union(IMAGE_EXTENSIONS, DOC_EXTENSIONS, VIDEO_EXTENSIONS);
    /** 最大文件大小 50MB */
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 单文件上传
     * @param bizType 业务类型（可选）：vehicle_image / avatar / document 等
     */
    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", required = false) String bizType,
            HttpServletRequest request) {
        Map<String, Object> result = doUpload(file, bizType, request);
        return Result.ok(result);
    }

    /**
     * 多文件上传
     */
    @PostMapping("/images")
    public Result<List<Map<String, Object>>> uploadImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "bizType", required = false) String bizType,
            HttpServletRequest request) {
        if (files == null || files.length == 0) {
            throw new BusinessException("请至少选择一个文件");
        }
        List<Map<String, Object>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(doUpload(file, bizType, request));
        }
        return Result.ok(results);
    }

    /**
     * 执行上传并写入数据库
     * 返回 Map 包含：url, fileId, originalName, size, extension, category
     */
    private Map<String, Object> doUpload(MultipartFile file, String bizType, HttpServletRequest request) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String category = resolveCategory(extension);
        String newFilename = IdUtil.simpleUUID() + "." + extension;

        // 确保目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new BusinessException("创建上传目录失败");
        }

        // 保存文件到磁盘
        // 使用 file.getBytes() + FileOutputStream 写入，规避 Spring transferTo
        // 和 NIO Files.copy 在 Windows 下的权限问题。
        File dest = new File(uploadDir, newFilename);
        try {
            byte[] bytes = file.getBytes();
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(bytes);
            }
        } catch (IOException e) {
            log.error("文件上传失败: class={}, msg={}, path={}, dirExists={}, dirWritable={}",
                    e.getClass().getName(), e.getMessage(), dest.getAbsolutePath(),
                    uploadDir.exists(), uploadDir.canWrite(), e);
            throw new BusinessException("文件上传失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // 构造访问 URL
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String url = baseUrl + "/uploads/" + newFilename;
        String absolutePath = dest.getAbsolutePath();

        // 获取上传人信息（优先使用昵称，其次用户名）
        Long uploaderId = securityUtil.getCurrentUserId();
        String uploaderName = resolveUploaderName(uploaderId);

        // 写入 sys_file 表
        SysFile sysFile = new SysFile();
        sysFile.setOriginalName(originalFilename);
        sysFile.setStoredName(newFilename);
        sysFile.setUrl(url);
        sysFile.setPath(absolutePath);
        sysFile.setSize(file.getSize());
        sysFile.setExtension(extension);
        sysFile.setMimeType(file.getContentType());
        sysFile.setCategory(category);
        sysFile.setBizType(StringUtils.hasText(bizType) ? bizType : null);
        sysFile.setUploadedBy(uploaderId);
        sysFile.setUploadedByName(uploaderName);
        sysFile.setStatus(1);
        sysFile.setCreatedAt(LocalDateTime.now());
        sysFileService.add(sysFile);

        return Map.of(
                "url", url,
                "fileId", sysFile.getId(),
                "originalName", originalFilename,
                "size", file.getSize(),
                "extension", extension,
                "category", category,
                "path", absolutePath
        );
    }

    /**
     * 根据 userId 解析上传人显示名：
     * 优先返回 nickname（中文名），其次 username（登录账号），都查不到时返回 null
     */
    private String resolveUploaderName(Long userId) {
        if (userId == null) return null;
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null) {
                if (StringUtils.hasText(user.getNickname())) {
                    return user.getNickname();
                }
                return user.getUsername();
            }
        } catch (Exception e) {
            log.warn("解析上传人名称失败, userId={}, err={}", userId, e.getMessage());
        }
        return null;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过50MB");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }
        String extension = getExtension(originalFilename);
        if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    /** 根据扩展名推断业务分类 */
    private String resolveCategory(String extension) {
        String ext = extension.toLowerCase();
        if (IMAGE_EXTENSIONS.contains(ext)) return "image";
        if (DOC_EXTENSIONS.contains(ext)) return "document";
        if (VIDEO_EXTENSIONS.contains(ext)) return "video";
        return "other";
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return filename.substring(dotIndex + 1);
    }

    @SafeVarargs
    private static Set<String> union(Set<String>... sets) {
        java.util.Set<String> result = new java.util.HashSet<>();
        for (Set<String> s : sets) result.addAll(s);
        return result;
    }
}
