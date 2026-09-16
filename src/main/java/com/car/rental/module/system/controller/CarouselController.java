package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Carousel;
import com.car.rental.module.system.mapper.CarouselMapper;
import com.car.rental.module.system.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "轮播图管理", description = "首页轮播图的增删改查、上下架状态维护与当前上架列表查询")
@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
public class CarouselController {

    private final CarouselService carouselService;

    @Operation(summary = "轮播图列表（分页）", description = "说明：分页查询轮播图，支持标题关键字与状态筛选。需要 settings:carousel 权限")
    @GetMapping("/list")
    @RequirePermission("settings:carousel")
    public Result<PageResult<Carousel>> getList(
            @Parameter(description = "页码，从 1 开始") @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "标题模糊匹配") @RequestParam(required = false) String title,
            @Parameter(description = "状态筛选（1 上架 / 0 下架）") @RequestParam(required = false) Integer status) {
        IPage<Carousel> page = carouselService.getPageList(pageNum, pageSize, title, status);
        return Result.ok(PageResult.of(page));
    }

    @Operation(summary = "当前上架轮播图", description = "说明：返回上架中（status=1 且处于上架时间窗口内）的轮播图，按 sort_order 升序，供首页展示，无需专门权限")
    @GetMapping("/active")
    public Result<?> getActive() {
        return Result.ok(carouselService.getActive());
    }

    @Operation(summary = "轮播图详情", description = "说明：按 ID 查询单个轮播图信息。需要 settings:carousel 权限")
    @GetMapping("/{id}")
    @RequirePermission("settings:carousel")
    public Result<?> getById(@Parameter(description = "轮播图ID") @PathVariable Long id) {
        return Result.ok(carouselService.getById(id));
    }

    @Operation(summary = "新增轮播图", description = "说明：新建轮播图，含标题、图片、跳转链接、排序与上下架时间。需要 settings:carousel:add 权限")
    @PostMapping("/add")
    @RequirePermission("settings:carousel:add")
    @LogChanges(
        entityClass = Carousel.class,
        mapperClass = CarouselMapper.class,
        mode = LogChanges.Mode.ADD,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "title:标题", "description:描述", "imageUrl:图片地址",
            "linkUrl:跳转链接", "sortOrder:排序", "status:状态",
            "startTime:上架时间", "endTime:下架时间"
        }
    )
    public Result<?> add(@RequestBody Carousel carousel) {
        carouselService.addCarousel(carousel);
        return Result.ok();
    }

    @Operation(summary = "编辑轮播图", description = "说明：修改轮播图的标题、图片、跳转链接、排序与上下架时间。需要 settings:carousel:update 权限")
    @PutMapping("/update")
    @RequirePermission("settings:carousel:update")
    @LogChanges(
        entityClass = Carousel.class,
        mapperClass = CarouselMapper.class,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "title:标题", "description:描述", "imageUrl:图片地址",
            "linkUrl:跳转链接", "sortOrder:排序", "status:状态",
            "startTime:上架时间", "endTime:下架时间"
        }
    )
    public Result<?> update(@RequestBody Carousel carousel) {
        carouselService.updateCarousel(carousel);
        return Result.ok();
    }

    @Operation(summary = "删除轮播图", description = "说明：按 ID 删除轮播图。需要 settings:carousel:delete 权限")
    @DeleteMapping("/{id}")
    @RequirePermission("settings:carousel:delete")
    @LogChanges(
        entityClass = Carousel.class,
        mapperClass = CarouselMapper.class,
        mode = LogChanges.Mode.DELETE,
        ignoreFields = {"createdAt"},
        fieldLabels = {
            "title:标题", "description:描述", "imageUrl:图片地址",
            "linkUrl:跳转链接", "sortOrder:排序", "status:状态",
            "startTime:上架时间", "endTime:下架时间"
        }
    )
    public Result<?> delete(@Parameter(description = "轮播图ID") @PathVariable Long id) {
        carouselService.deleteCarousel(id);
        return Result.ok();
    }

    @Operation(summary = "轮播图上下架", description = "说明：body 传 status（1 上架 / 0 下架）修改状态。需要 settings:carousel:status 权限")
    @PutMapping("/{id}/status")
    @RequirePermission("settings:carousel:status")
    public Result<?> updateStatus(@Parameter(description = "轮播图ID") @PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : null;
        carouselService.updateStatus(id, status);
        return Result.ok();
    }
}
