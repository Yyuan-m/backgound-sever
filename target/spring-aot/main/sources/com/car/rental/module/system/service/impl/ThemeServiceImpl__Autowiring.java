package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ThemeServiceImpl}.
 */
@Generated
public class ThemeServiceImpl__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ThemeServiceImpl apply(RegisteredBean registeredBean, ThemeServiceImpl instance) {
    AutowiredFieldValueResolver.forRequiredField("sysThemeConfigMapper").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
