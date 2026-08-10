package com.car.rental.module.system.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link RoleController}.
 */
@Generated
public class RoleController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static RoleController apply(RegisteredBean registeredBean, RoleController instance) {
    AutowiredFieldValueResolver.forRequiredField("roleService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
