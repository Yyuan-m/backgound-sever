package com.car.rental.module.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.GpsTrack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GpsTrackMapper extends BaseMapper<GpsTrack> {

    @Select("SELECT * FROM gps_track WHERE vehicle_id = #{vehicleId} ORDER BY timestamp DESC")
    List<GpsTrack> selectByVehicleId(@Param("vehicleId") Long vehicleId);

    @Select("SELECT * FROM gps_track WHERE vehicle_id = #{vehicleId} ORDER BY timestamp DESC LIMIT 1")
    GpsTrack selectLatest(@Param("vehicleId") Long vehicleId);

    @Select("<script>" +
            "SELECT * FROM gps_track WHERE 1=1" +
            "<if test='vehicleId != null'> AND vehicle_id = #{vehicleId}</if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            " ORDER BY timestamp DESC" +
            " LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<GpsTrack> selectByConditions(@Param("offset") int offset,
                              @Param("pageSize") int pageSize,
                              @Param("vehicleId") Long vehicleId,
                              @Param("status") String status);
}