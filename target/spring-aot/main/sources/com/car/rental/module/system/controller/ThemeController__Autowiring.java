package com.car.rental.module.system.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ThemeController}.
 */
@Generated
public class ThemeController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ThemeController apply(RegisteredBean registeredBean, ThemeController instance) {
    AutowiredFieldValueResolver.forRequiredField("themeService").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("securityUtil").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
