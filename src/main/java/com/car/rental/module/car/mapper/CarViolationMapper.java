package com.car.rental.module.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.CarViolation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarViolationMapper extends BaseMapper<CarViolation> {

    @Select("<script>" +
            "SELECT * FROM car_violation WHERE 1=1" +
            "<if test='vehicleId != null'> AND vehicle_id = #{vehicleId}</if>" +
            "<if test='orderId != null'> AND order_id = #{orderId}</if>" +
            "<if test='violationType != null and violationType != \"\"'> AND violation_type = #{violationType}</if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<CarViolation> selectByConditions(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize,
                                  @Param("vehicleId") Long vehicleId,
                                  @Param("orderId") Long orderId,
                                  @Param("violationType") String violationType,
                                  @Param("status") String status);
}