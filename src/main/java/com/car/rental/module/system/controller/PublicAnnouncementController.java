package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Announcement;
import com.car.rental.module.system.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告 C 端公开接口
 * 路径走 /api/public/**，由 SecurityConfig 放行，无需登录 token。
 * 仅返回已发布（status=1）且未删除的公告，按优先级 high > normal > low + 创建时间倒序排序。
 */
@RestController
@RequestMapping("/api/public/announcement")
@RequiredArgsConstructor
public class PublicAnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * C 端分页查询公告列表
     *
     * @param pageNum  页码，默认 1
     * @param pageSize 每页条数，默认 10
     * @param priority 可选，按优先级过滤：high / normal / low
     */
    @GetMapping("/list")
    public Result<PageResult<Announcement>> list(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String priority) {
        IPage<Announcement> page = announcementService.getPublicPageList(pageNum, pageSize, priority);
        return Result.ok(PageResult.of(page));
    }
}
