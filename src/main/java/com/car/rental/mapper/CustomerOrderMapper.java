package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.CustomerOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerOrderMapper extends BaseMapper<CustomerOrder> {
}
