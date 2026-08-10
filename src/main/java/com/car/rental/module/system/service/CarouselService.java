package com.car.rental.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.Carousel;

import java.util.List;

public interface CarouselService {

    IPage<Carousel> getPageList(long pageNum, long pageSize, String title, Integer status);

    Carousel getById(Long id);

    List<Carousel> getActive();

    void addCarousel(Carousel carousel);

    void updateCarousel(Carousel carousel);

    void deleteCarousel(Long id);

    void updateStatus(Long id, Integer status);
}