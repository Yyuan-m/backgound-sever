package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Announcement;

public interface AnnouncementService {

    IPage<Announcement> getPageList(long pageNum, long pageSize, String keyword, String priority);

    Announcement getById(Long id);

    void addAnnouncement(Announcement announcement);

    void updateAnnouncement(Announcement announcement);

    void deleteAnnouncement(Long id);
}