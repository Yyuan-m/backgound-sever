package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.SysFile;

public interface SysFileService {

    /** 分页查询文件列表 */
    PageResult<SysFile> list(Integer pageNum, Integer pageSize,
                             String originalName, String category, String bizType,
                             Long uploadedBy, Integer status);

    /** 获取文件详情 */
    SysFile getById(Long id);

    /** 保存文件记录（上传成功后调用） */
    void add(SysFile sysFile);

    /** 逻辑删除文件（移入回收站） */
    void delete(Long id);

    /** 批量逻辑删除（移入回收站） */
    void batchDelete(java.util.List<Long> ids);

    /** 物理删除文件（同时删除磁盘文件，谨慎使用） */
    void physicalDelete(Long id);

    /** 批量物理删除（同时删除磁盘文件） */
    void batchPhysicalDelete(java.util.List<Long> ids);

    /** 恢复文件（从回收站恢复为正常状态） */
    void restore(Long id);

    /** 批量恢复文件 */
    void batchRestore(java.util.List<Long> ids);
}
