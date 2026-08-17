package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Announcement;

public interface AnnouncementService {

    IPage<Announcement> getPageList(long pageNum, long pageSize, String keyword, String priority);

    /**
     * C 端公开分页查询：仅返回已发布（status=1）且未删除的公告
     * 排序规则：优先级 high > normal > low，同优先级按创建时间倒序
     */
    IPage<Announcement> getPublicPageList(long pageNum, long pageSize, String priority);

    Announcement getById(Long id);

    void addAnnouncement(Announcement announcement);

    void updateAnnouncement(Announcement announcement);

    void deleteAnnouncement(Long id);
}