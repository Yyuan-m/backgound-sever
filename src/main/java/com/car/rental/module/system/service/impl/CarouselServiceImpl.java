package com.car.rental.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.entity.Carousel;
import com.car.rental.module.system.mapper.CarouselMapper;
import com.car.rental.module.system.service.CarouselService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarouselServiceImpl implements CarouselService {

    private final CarouselMapper carouselMapper;

    @Override
    public IPage<Carousel> getPageList(long pageNum, long pageSize, String title, Integer status) {
        Page<Carousel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(Carousel::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Carousel::getStatus, status);
        }
        wrapper.orderByAsc(Carousel::getSortOrder);
        return carouselMapper.selectPage(page, wrapper);
    }

    @Override
    public Carousel getById(Long id) {
        Carousel carousel = carouselMapper.selectById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }
        return carousel;
    }

    @Override
    public List<Carousel> getActive() {
        return carouselMapper.selectActive();
    }

    @Override
    @Transactional
    public void addCarousel(Carousel carousel) {
        carousel.setCreatedAt(LocalDateTime.now());
        carouselMapper.insert(carousel);
    }

    @Override
    @Transactional
    public void updateCarousel(Carousel carousel) {
        Carousel existing = carouselMapper.selectById(carousel.getId());
        if (existing == null) {
            throw new BusinessException("轮播图不存在");
        }
        existing.setTitle(carousel.getTitle());
        existing.setDescription(carousel.getDescription());
        existing.setImageUrl(carousel.getImageUrl());
        existing.setLinkUrl(carousel.getLinkUrl());
        existing.setSortOrder(carousel.getSortOrder());
        if (carousel.getStatus() != null) {
            existing.setStatus(carousel.getStatus());
        }
        carouselMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteCarousel(Long id) {
        Carousel carousel = carouselMapper.selectById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }
        carouselMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Carousel carousel = carouselMapper.selectById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }
        carouselMapper.updateStatus(id, status);
    }
}