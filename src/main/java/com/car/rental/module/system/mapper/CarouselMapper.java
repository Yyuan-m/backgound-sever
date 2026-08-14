package com.car.rental.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.Carousel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CarouselMapper extends BaseMapper<Carousel> {

    @Select("SELECT * FROM carousel WHERE status = 1 " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY sort_order ASC")
    List<Carousel> selectActive();

    @Update("UPDATE carousel SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}