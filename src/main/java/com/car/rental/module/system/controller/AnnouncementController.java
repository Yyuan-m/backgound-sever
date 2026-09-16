package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Announcement;
import com.car.rental.mapper.AnnouncementMapper;
import com.car.rental.module.system.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "公告管理", description = "后台公告的增删改查，支持标题关键字与优先级筛选")
@RestController
@RequestMapping("/api/system/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @Operation(summary = "公告列表（分页）", description = "说明：分页查询公告，按创建时间倒序；支持标题关键字与优先级筛选。需要 settings:announcements 权限")
    @GetMapping("/list")
    @RequirePermission("settings:announcements")
    public Result<PageResult<Announcement>> getList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键字：标题模糊匹配") @RequestParam(required = false) String keyword,
            @Parameter(description = "优先级筛选：high / normal / low") @RequestParam(required = false) String priority) {
        IPage<Announcement> page = announcementService.getPageList(pageNum, pageSize, keyword, priority);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "公告详情", description = "说明：按 ID 查询公告完整内容。需要 settings:announcements 权限")
    @GetMapping("/{id}")
    @RequirePermission("settings:announcements")
    public Result<?> getById(@Parameter(description = "公告ID") @PathVariable Long id) {
        return Result.ok(announcementService.getById(id));
    }

    @Operation(summary = "新增公告", description = "说明：新建公告，含标题、内容、优先级与发布状态。需要 settings:announcements:add 权限")
    @PostMapping("/add")
    @RequirePermission("settings:announcements:add")
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

    @Operation(summary = "编辑公告", description = "说明：修改公告的标题、内容、优先级与发布状态。需要 settings:announcements:update 权限")
    @PutMapping("/update")
    @RequirePermission("settings:announcements:update")
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

    @Operation(summary = "删除公告", description = "说明：按 ID 删除公告。需要 settings:announcements:delete 权限")
    @DeleteMapping("/{id}/delete")
    @RequirePermission("settings:announcements:delete")
    @LogChanges(
        entityClass = Announcement.class,
        mapperClass = AnnouncementMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt", "updatedAt", "isDelete"},
        fieldLabels = {
            "title:标题", "content:内容", "priority:优先级", "status:状态"
        }
    )
    public Result<?> delete(@Parameter(description = "公告ID") @PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.ok();
    }
}
