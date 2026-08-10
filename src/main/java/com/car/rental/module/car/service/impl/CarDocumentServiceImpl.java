package com.car.rental.module.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarDocument;
import com.car.rental.module.car.mapper.CarDocumentMapper;
import com.car.rental.module.car.service.CarDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarDocumentServiceImpl implements CarDocumentService {

    private final CarDocumentMapper carDocumentMapper;

    @Override
    public PageResult<CarDocument> getList(Integer pageNum, Integer pageSize, Long vehicleId, String docType, String status) {
        Page<CarDocument> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<CarDocument> wrapper = new LambdaQueryWrapper<>();

        if (vehicleId != null) {
            wrapper.eq(CarDocument::getVehicleId, vehicleId);
        }
        if (StringUtils.hasText(docType)) {
            wrapper.eq(CarDocument::getDocType, docType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(CarDocument::getStatus, status);
        }
        wrapper.orderByDesc(CarDocument::getCreatedAt);

        IPage<CarDocument> result = carDocumentMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public CarDocument getById(Long id) {
        CarDocument document = carDocumentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException("车辆证件不存在");
        }
        return document;
    }

    @Override
    @Transactional
    public void add(CarDocument carDocument) {
        carDocumentMapper.insert(carDocument);
    }

    @Override
    @Transactional
    public void update(CarDocument carDocument) {
        CarDocument existing = carDocumentMapper.selectById(carDocument.getId());
        if (existing == null) {
            throw new BusinessException("车辆证件不存在");
        }
        carDocumentMapper.updateById(carDocument);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CarDocument existing = carDocumentMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("车辆证件不存在");
        }
        carDocumentMapper.deleteById(id);
    }

    @Override
    public List<CarDocument> getByVehicleId(Long vehicleId) {
        LambdaQueryWrapper<CarDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarDocument::getVehicleId, vehicleId);
        wrapper.orderByDesc(CarDocument::getCreatedAt);
        return carDocumentMapper.selectList(wrapper);
    }
}