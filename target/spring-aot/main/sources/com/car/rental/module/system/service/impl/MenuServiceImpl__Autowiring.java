package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link MenuServiceImpl}.
 */
@Generated
public class MenuServiceImpl__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static MenuServiceImpl apply(RegisteredBean registeredBean, MenuServiceImpl instance) {
    AutowiredFieldValueResolver.forRequiredField("sysMenuMapper").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
