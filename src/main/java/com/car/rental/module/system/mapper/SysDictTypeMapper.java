package com.car.rental.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.SysDictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    @Select("SELECT * FROM sys_dict_type WHERE dict_type = #{dictType}")
    SysDictType selectByType(String dictType);
}