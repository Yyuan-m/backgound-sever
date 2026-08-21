package com.car.rental.module.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.exception.BusinessException;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarConfig;
import com.car.rental.entity.CarInfo;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.module.car.mapper.CarConfigMapper;
import com.car.rental.module.car.service.CarImageService;
import com.car.rental.module.car.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarInfoMapper carInfoMapper;
    private final CarConfigMapper carConfigMapper;
    private final CarImageService carImageService;

    @Override
    public PageResult<CarInfo> list(Integer pageNum, Integer pageSize, String keyword, String type, String status) {
        Page<CarInfo> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        // 车辆状态实时计算：存在待支付(pending)/租赁中(renting)订单的车辆显示为 rented，
        // 不再直接使用 car_info.status 持久化字段（订单流转不回写该字段，直接查会显示错误的"空闲"）
        String kw = StringUtils.hasText(keyword) ? keyword : null;
        String tp = StringUtils.hasText(type) ? type : null;
        String st = StringUtils.hasText(status) ? status : null;
        IPage<CarInfo> result = carInfoMapper.selectPageWithRealtimeStatus(page, kw, tp, st);
        // 批量填充每辆车的素材数量，便于列表展示
        List<CarInfo> records = result.getRecords();
        if (records != null && !records.isEmpty()) {
            List<Long> vehicleIds = records.stream().map(CarInfo::getId).collect(Collectors.toList());
            Map<Long, Long> countMap = carImageService.countByVehicleIds(vehicleIds);
            for (CarInfo car : records) {
                long cnt = countMap.getOrDefault(car.getId(), 0L);
                car.setMaterialCount((int) cnt);
            }
        }
        return PageResult.of(result);
    }

    @Override
    public CarInfo detail(Long id) {
        CarInfo carInfo = carInfoMapper.selectById(id);
        if (carInfo == null) {
            throw new BusinessException("车辆不存在");
        }
        // 关联查询车辆配置
        LambdaQueryWrapper<CarConfig> cfgWrapper = new LambdaQueryWrapper<>();
        cfgWrapper.eq(CarConfig::getCarId, id);
        CarConfig carConfig = carConfigMapper.selectOne(cfgWrapper);
        carInfo.setCarConfig(carConfig);
        // 关联查询该车辆的全部素材（详情页只读展示）
        carInfo.setMaterials(carImageService.listByVehicleId(id));
        return carInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CarInfo carInfo) {
        LambdaQueryWrapper<CarInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarInfo::getPlateNumber, carInfo.getPlateNumber());
        if (carInfoMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("车牌号已存在");
        }
        // 日成本价：未手动填写时自动 = 日租金 × 0.54
        calcDailyCostIfAbsent(carInfo);
        carInfoMapper.insert(carInfo);
        // 新增车辆时一并保存配置（若前端传入了 carConfig）
        saveOrUpdateConfig(carInfo.getId(), carInfo.getCarConfig());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, CarInfo carInfo) {
        CarInfo existing = carInfoMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("车辆不存在");
        }
        if (StringUtils.hasText(carInfo.getPlateNumber()) && !carInfo.getPlateNumber().equals(existing.getPlateNumber())) {
            LambdaQueryWrapper<CarInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CarInfo::getPlateNumber, carInfo.getPlateNumber());
            if (carInfoMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("车牌号已存在");
            }
        }
        carInfo.setId(id);
        // 日成本价：未手动填写时自动 = 日租金 × 0.54
        calcDailyCostIfAbsent(carInfo);
        carInfoMapper.updateById(carInfo);
        // 最大租期：updateById 默认忽略 null 字段，若需将"有限制"改回"不限制"，
        // 需把 max_rent_days 显式置 NULL 覆盖旧值
        if (carInfo.getMaxRentDays() == null) {
            LambdaUpdateWrapper<CarInfo> nullWrapper = new LambdaUpdateWrapper<>();
            nullWrapper.eq(CarInfo::getId, id).set(CarInfo::getMaxRentDays, null);
            carInfoMapper.update(null, nullWrapper);
        }
        // 更新车辆时一并保存/更新配置
        saveOrUpdateConfig(id, carInfo.getCarConfig());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        CarInfo existing = carInfoMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("车辆不存在");
        }
        carInfoMapper.deleteById(id);
        // 删除车辆时一并删除配置
        LambdaQueryWrapper<CarConfig> cfgWrapper = new LambdaQueryWrapper<>();
        cfgWrapper.eq(CarConfig::getCarId, id);
        carConfigMapper.delete(cfgWrapper);
    }

    @Override
    public void updateStatus(Long id, String status) {
        CarInfo existing = carInfoMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("车辆不存在");
        }
        CarInfo update = new CarInfo();
        update.setId(id);
        update.setStatus(status);
        carInfoMapper.updateById(update);
    }

    /**
     * 保存或更新车辆配置：
     * - 若 config 为 null，表示前端未提交配置，不处理
     * - 若数据库已有配置记录，则更新；否则插入新记录
     * - 通过 carId 唯一约束保证一对一关系
     */
    private void saveOrUpdateConfig(Long carId, CarConfig config) {
        if (config == null) {
            return;
        }
        config.setCarId(carId);
        LambdaQueryWrapper<CarConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarConfig::getCarId, carId);
        CarConfig existing = carConfigMapper.selectOne(wrapper);
        if (existing != null) {
            config.setId(existing.getId());
            carConfigMapper.updateById(config);
        } else {
            config.setId(null);
            carConfigMapper.insert(config);
        }
    }

    /**
     * 日成本价自动计算：若 dailyCost 为空但 dailyPrice 有值，则 dailyCost = dailyPrice × 0.54
     * 若用户手动填写了 dailyCost，则尊重用户输入不覆盖
     */
    private void calcDailyCostIfAbsent(CarInfo carInfo) {
        if (carInfo.getDailyCost() == null && carInfo.getDailyPrice() != null) {
            carInfo.setDailyCost(carInfo.getDailyPrice().multiply(new java.math.BigDecimal("0.54"))
                    .setScale(2, java.math.RoundingMode.HALF_UP));
        }
    }
}
