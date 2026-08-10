package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.SysFile;
import com.car.rental.module.system.mapper.SysFileMapper;
import com.car.rental.module.system.service.SysFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl implements SysFileService {

    private final SysFileMapper sysFileMapper;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public PageResult<SysFile> list(Integer pageNum, Integer pageSize,
                                    String originalName, String category, String bizType,
                                    Long uploadedBy, Integer status) {
        Page<SysFile> page = new Page<>(
                pageNum != null ? pageNum : 1,
                pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(originalName)) {
            wrapper.like(SysFile::getOriginalName, originalName);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(SysFile::getCategory, category);
        }
        if (StringUtils.hasText(bizType)) {
            wrapper.eq(SysFile::getBizType, bizType);
        }
        if (uploadedBy != null) {
            wrapper.eq(SysFile::getUploadedBy, uploadedBy);
        }
        // status=null 不加过滤（查全部），status=0/1 精确匹配
        if (status != null) {
            wrapper.eq(SysFile::getStatus, status);
        }
        wrapper.orderByDesc(SysFile::getCreatedAt);
        IPage<SysFile> result = sysFileMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public SysFile getById(Long id) {
        SysFile file = sysFileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        return file;
    }

    @Override
    @Transactional
    public void add(SysFile sysFile) {
        sysFile.setCreatedAt(LocalDateTime.now());
        if (sysFile.getStatus() == null) {
            sysFile.setStatus(1);
        }
        sysFileMapper.insert(sysFile);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysFile file = sysFileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        file.setStatus(0);
        sysFileMapper.updateById(file);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        for (Long id : ids) {
            SysFile file = sysFileMapper.selectById(id);
            if (file != null) {
                file.setStatus(0);
                sysFileMapper.updateById(file);
            }
        }
    }

    @Override
    @Transactional
    public void physicalDelete(Long id) {
        SysFile file = sysFileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        // 删除磁盘文件
        if (StringUtils.hasText(file.getPath())) {
            File diskFile = new File(file.getPath());
            if (diskFile.exists() && !diskFile.delete()) {
                log.warn("磁盘文件删除失败: {}", file.getPath());
            }
        }
        sysFileMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchPhysicalDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        for (Long id : ids) {
            SysFile file = sysFileMapper.selectById(id);
            if (file == null) continue;
            // 删除磁盘文件
            if (StringUtils.hasText(file.getPath())) {
                File diskFile = new File(file.getPath());
                if (diskFile.exists() && !diskFile.delete()) {
                    log.warn("磁盘文件删除失败: {}", file.getPath());
                }
            }
            sysFileMapper.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void restore(Long id) {
        SysFile file = sysFileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        file.setStatus(1);
        sysFileMapper.updateById(file);
    }

    @Override
    @Transactional
    public void batchRestore(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        for (Long id : ids) {
            SysFile file = sysFileMapper.selectById(id);
            if (file != null) {
                file.setStatus(1);
                sysFileMapper.updateById(file);
            }
        }
    }
}
