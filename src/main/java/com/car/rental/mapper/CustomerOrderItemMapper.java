package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.CustomerOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单车辆明细 Mapper（一单可含多车）
 */
@Mapper
public interface CustomerOrderItemMapper extends BaseMapper<CustomerOrderItem> {
}