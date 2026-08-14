package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.CustomerOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerOrderMapper extends BaseMapper<CustomerOrder> {

    /**
     * 按状态统计订单数量（全量，不含已删除订单）
     * 使用原生 SQL 避免 MyBatis-Plus QueryWrapper 在 selectMaps + 聚合函数场景下的解析差异
     */
    @Select("SELECT status AS status, COUNT(*) AS cnt FROM customer_order WHERE is_delete = 0 GROUP BY status")
    List<Map<String, Object>> selectStatusCount();
}
