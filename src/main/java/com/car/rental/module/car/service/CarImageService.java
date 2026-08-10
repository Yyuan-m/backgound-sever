package com.car.rental.module.car.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarImage;

import java.util.List;
import java.util.Map;

public interface CarImageService {

    PageResult<CarImage> list(Integer pageNum, Integer pageSize, Long vehicleId, String category);

    void add(CarImage carImage);

    void delete(Long id);

    /**
     * 按车辆ID查询全部启用的素材（不分页，按创建时间倒序）
     * 供车辆详情接口使用
     */
    List<CarImage> listByVehicleId(Long vehicleId);

    /**
     * 批量统计多个车辆的素材数量（仅统计 status=1 的记录）
     * @param vehicleIds 车辆ID列表
     * @return vehicleId -> 素材数量
     */
    Map<Long, Long> countByVehicleIds(List<Long> vehicleIds);
}
