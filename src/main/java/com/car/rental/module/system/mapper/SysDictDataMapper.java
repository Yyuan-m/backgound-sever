package com.car.rental.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    @Select("SELECT * FROM sys_dict_data WHERE dict_type = #{dictType} AND status = 1 ORDER BY sort_order ASC")
    List<SysDictData> selectByType(String dictType);
}