package com.car.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /** 根据用户名和邮箱校验用户是否存在（用于找回密码身份验证） */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND email = #{email} AND is_delete = 0 LIMIT 1")
    SysUser findByUsernameAndEmail(@Param("username") String username, @Param("email") String email);
}
