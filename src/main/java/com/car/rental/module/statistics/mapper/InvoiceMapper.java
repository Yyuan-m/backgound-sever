package com.car.rental.module.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.Invoice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvoiceMapper extends BaseMapper<Invoice> {
}
