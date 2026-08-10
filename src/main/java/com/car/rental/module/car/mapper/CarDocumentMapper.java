package com.car.rental.module.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.CarDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarDocumentMapper extends BaseMapper<CarDocument> {

    @Select("<script>" +
            "SELECT * FROM car_document WHERE 1=1" +
            "<if test='vehicleId != null'> AND vehicle_id = #{vehicleId}</if>" +
            "<if test='docType != null and docType != \"\"'> AND doc_type = #{docType}</if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            " ORDER BY created_at DESC" +
            " LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<CarDocument> selectByConditions(@Param("offset") int offset,
                                 @Param("pageSize") int pageSize,
                                 @Param("vehicleId") Long vehicleId,
                                 @Param("docType") String docType,
                                 @Param("status") String status);
}