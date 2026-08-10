package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link SysFileServiceImpl}.
 */
@Generated
public class SysFileServiceImpl__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static SysFileServiceImpl apply(RegisteredBean registeredBean,
      SysFileServiceImpl instance) {
    AutowiredFieldValueResolver.forRequiredField("uploadPath").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
