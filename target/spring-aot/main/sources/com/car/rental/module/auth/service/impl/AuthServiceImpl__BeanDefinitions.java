package com.car.rental.module.auth.service.impl;

import com.car.rental.common.util.JwtUtil;
import com.car.rental.mapper.SysRoleMapper;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.mapper.SysUserRoleMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Bean definitions for {@link AuthServiceImpl}.
 */
@Generated
public class AuthServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'authServiceImpl'.
   */
  private static BeanInstanceSupplier<AuthServiceImpl> getAuthServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<AuthServiceImpl>forConstructor(SysUserMapper.class, SysRoleMapper.class, SysUserRoleMapper.class, JwtUtil.class, RedisTemplate.class, StringRedisTemplate.class)
            .withGenerator((registeredBean, args) -> new AuthServiceImpl(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5)));
  }

  /**
   * Get the bean definition for 'authServiceImpl'.
   */
  public static BeanDefinition getAuthServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AuthServiceImpl.class);
    beanDefinition.setInstanceSupplier(getAuthServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
