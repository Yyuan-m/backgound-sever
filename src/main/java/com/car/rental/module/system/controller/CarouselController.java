package com.car.rental.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.common.annotation.LogChanges;
import com.car.rental.common.annotation.RequirePermission;
import com.car.rental.common.result.PageResult;
import com.car.rental.common.result.Result;
import com.car.rental.entity.Carousel;
import com.car.rental.module.system.mapper.CarouselMapper;
import com.car.rental.module.system.service.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
public class CarouselController {

    private final CarouselService carouselService;

    @GetMapping("/list")
    @RequirePermission("settings:carousel")
    public Result<PageResult<Carousel>> getList(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        IPage<Carousel> page = carouselService.getPageList(pageNum, pageSize, title, status);
        return Result.ok(PageResult.of(page));
    }

    @GetMapping("/active")
    public Result<?> getActive() {
        return Result.ok(carouselService.getActive());
    }

    @GetMapping("/{id}")
    @RequirePermission("settings:carousel")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(carouselService.getById(id));
    }

    @PostMapping("/add")
    @RequirePermission("settings:carousel")
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

    @PutMapping("/update")
    @RequirePermission("settings:carousel")
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

    @DeleteMapping("/{id}")
    @RequirePermission("settings:carousel")
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
    public Result<?> delete(@PathVariable Long id) {
        carouselService.deleteCarousel(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @RequirePermission("settings:carousel")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : null;
        carouselService.updateStatus(id, status);
        return Result.ok();
    }
}