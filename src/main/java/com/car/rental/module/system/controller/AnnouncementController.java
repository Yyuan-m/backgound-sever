package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Announcement;
import com.car.rental.mapper.AnnouncementMapper;
import com.car.rental.module.system.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/list")
    @RequirePermission("settings:announcements")
    public Result<PageResult<Announcement>> getList(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String priority) {
        IPage<Announcement> page = announcementService.getPageList(pageNum, pageSize, keyword, priority);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @RequirePermission("settings:announcements")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(announcementService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("settings:announcements")
    @LogChanges(
        entityClass = Announcement.class,
        mapperClass = AnnouncementMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "title:标题", "content:内容", "priority:优先级", "status:状态"
        }
    )
    public Result<?> add(@RequestBody Announcement announcement) {
        announcementService.addAnnouncement(announcement);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequirePermission("settings:announcements")
    @LogChanges(
        entityClass = Announcement.class,
        mapperClass = AnnouncementMapper.class,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "title:标题", "content:内容", "priority:优先级", "status:状态"
        }
    )
    public Result<?> update(@RequestBody Announcement announcement) {
        announcementService.updateAnnouncement(announcement);
        return Result.ok();
    }

    @DeleteMapping("/{id}/delete")
    @RequirePermission("settings:announcements")
    @LogChanges(
        entityClass = Announcement.class,
        mapperClass = AnnouncementMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "title:标题", "content:内容", "priority:优先级", "status:状态"
        }
    )
    public Result<?> delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.ok();
    }
}