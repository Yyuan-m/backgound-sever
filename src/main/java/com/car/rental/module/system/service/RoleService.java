package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.SysRole;

import java.util.List;

public interface RoleService {

    IPage<SysRole> getPageList(long pageNum, long pageSize, String keyword, Integer status);

    void addRole(SysRole role);

    void updateRole(SysRole role);

    void deleteRole(Long id);

    void toggleStatus(Long id);

    List<String> getPermissions(Long id);

    void savePermissions(Long id, List<String> permissions);

    SysRole getById(Long id);
}