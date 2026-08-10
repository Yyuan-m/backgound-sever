package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UserServiceImpl}.
 */
@Generated
public class UserServiceImpl__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UserServiceImpl apply(RegisteredBean registeredBean, UserServiceImpl instance) {
    AutowiredFieldValueResolver.forRequiredField("sysUserMapper").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("sysRoleMapper").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("sysUserRoleMapper").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
