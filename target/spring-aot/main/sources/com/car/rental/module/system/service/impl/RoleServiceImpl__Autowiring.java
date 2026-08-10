package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link RoleServiceImpl}.
 */
@Generated
public class RoleServiceImpl__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static RoleServiceImpl apply(RegisteredBean registeredBean, RoleServiceImpl instance) {
    AutowiredFieldValueResolver.forRequiredField("sysRoleMapper").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("sysUserRoleMapper").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
