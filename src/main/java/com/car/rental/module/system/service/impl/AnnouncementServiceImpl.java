package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.Announcement;
import com.car.rental.mapper.AnnouncementMapper;
import com.car.rental.module.system.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public IPage<Announcement> getPageList(long pageNum, long pageSize, String keyword, String priority) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Announcement::getTitle, keyword);
        }
        if (StringUtils.hasText(priority)) {
            wrapper.eq(Announcement::getPriority, priority);
        }
        wrapper.orderByDesc(Announcement::getCreatedAt);
        return announcementMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Announcement> getPublicPageList(long pageNum, long pageSize, String priority) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        // C 端只返回已发布且未删除的公告（is_delete 由 @TableLogic 自动过滤）
        wrapper.eq(Announcement::getStatus, 1);
        if (StringUtils.hasText(priority)) {
            wrapper.eq(Announcement::getPriority, priority);
        }
        // 优先级排序：high > normal > low，同优先级按创建时间倒序
        wrapper.last("ORDER BY FIELD(priority,'high','normal','low'), created_at DESC");
        return announcementMapper.selectPage(page, wrapper);
    }

    @Override
    public Announcement getById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        return announcement;
    }

    @Override
    @Transactional
    public void addAnnouncement(Announcement announcement) {
        announcement.setStatus(1);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.insert(announcement);
    }

    @Override
    @Transactional
    public void updateAnnouncement(Announcement announcement) {
        Announcement existing = announcementMapper.selectById(announcement.getId());
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setPriority(announcement.getPriority());
        if (announcement.getStatus() != null) {
            existing.setStatus(announcement.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        announcementMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        announcementMapper.deleteById(id);
    }
}