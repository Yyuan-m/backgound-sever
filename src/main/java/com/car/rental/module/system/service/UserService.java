package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.SysUser;

import java.util.List;

public interface UserService {

    IPage<SysUser> getPageList(long pageNum, long pageSize, String keyword, Integer status, String role);

    void addUser(SysUser user);

    void updateUser(SysUser user);

    void deleteUser(Long id);

    void toggleStatus(Long id);

    void resetPassword(Long id, String newPassword);

    void batchDelete(List<Long> ids);

    void batchToggleStatus(List<Long> ids, Integer status);

    SysUser getById(Long id);

    void changePassword(Long userId, String oldPassword, String newPassword);
}