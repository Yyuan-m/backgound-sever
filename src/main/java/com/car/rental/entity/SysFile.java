package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统文件实体
 * 记录所有通过 /api/upload 上传的文件元信息
 */
@Data
@TableName("sys_file")
public class SysFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalName;

    /** 存储文件名（UUID生成） */
    private String storedName;

    /** 访问URL */
    private String url;

    /** 服务器存储相对路径 */
    private String path;

    /** 文件大小(字节) */
    private Long size;

    /** 扩展名(不含.) */
    private String extension;

    /** MIME类型 */
    private String mimeType;

    /** 业务分类: image/document/video/other */
    private String category;

    /** 业务类型: vehicle_image/avatar/document 等 */
    private String bizType;

    /** 关联业务ID */
    private Long bizId;

    /** 上传用户ID */
    private Long uploadedBy;

    /** 上传用户名 */
    private String uploadedByName;

    /** 状态 1正常 0删除 */
    private Integer status;

    /** 上传时间 */
    private LocalDateTime createdAt;
}
