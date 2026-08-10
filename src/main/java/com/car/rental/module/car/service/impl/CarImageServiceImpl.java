package com.car.rental.module.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarImage;
import com.car.rental.entity.CarInfo;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.module.car.mapper.CarImageMapper;
import com.car.rental.module.car.service.CarImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarImageServiceImpl implements CarImageService {

    private final CarImageMapper carImageMapper;
    private final CarInfoMapper carInfoMapper;

    @Override
    public PageResult<CarImage> list(Integer pageNum, Integer pageSize, Long vehicleId, String category) {
        Page<CarImage> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<CarImage> wrapper = new LambdaQueryWrapper<>();
        if (vehicleId != null) {
            wrapper.eq(CarImage::getVehicleId, vehicleId);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(CarImage::getCategory, category);
        }
        wrapper.orderByDesc(CarImage::getCreatedAt);
        IPage<CarImage> result = carImageMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public void add(CarImage carImage) {
        if (carImage.getVehicleId() == null) {
            throw new BusinessException("请选择车辆");
        }
        if (!StringUtils.hasText(carImage.getUrl())) {
            throw new BusinessException("图片地址不能为空");
        }
        // 自动填充车辆名称
        if (!StringUtils.hasText(carImage.getVehicleName())) {
            CarInfo carInfo = carInfoMapper.selectById(carImage.getVehicleId());
            if (carInfo != null) {
                carImage.setVehicleName(carInfo.getName());
            }
        }
        if (carImage.getStatus() == null) {
            carImage.setStatus(1);
        }
        carImage.setCreatedAt(LocalDateTime.now());
        carImageMapper.insert(carImage);
    }

    @Override
    public void delete(Long id) {
        CarImage existing = carImageMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("素材不存在");
        }
        carImageMapper.deleteById(id);
    }

    @Override
    public List<CarImage> listByVehicleId(Long vehicleId) {
        if (vehicleId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<CarImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarImage::getVehicleId, vehicleId);
        wrapper.eq(CarImage::getStatus, 1);
        wrapper.orderByDesc(CarImage::getCreatedAt);
        return carImageMapper.selectList(wrapper);
    }

    @Override
    public Map<Long, Long> countByVehicleIds(List<Long> vehicleIds) {
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        QueryWrapper<CarImage> qw = new QueryWrapper<>();
        qw.select("vehicle_id", "count(*) as cnt");
        qw.in("vehicle_id", vehicleIds);
        qw.eq("status", 1);
        qw.groupBy("vehicle_id");
        List<Map<String, Object>> rows = carImageMapper.selectMaps(qw);
        Map<Long, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object vid = row.get("vehicle_id");
            Object cnt = row.get("cnt");
            if (vid != null && cnt != null) {
                result.put(Long.valueOf(vid.toString()), Long.valueOf(cnt.toString()));
            }
        }
        return result;
    }
}
