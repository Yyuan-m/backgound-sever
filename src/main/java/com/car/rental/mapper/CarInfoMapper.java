package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.CarInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarInfoMapper extends BaseMapper<CarInfo> {
}