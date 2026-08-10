package com.car.rental.module.system.controller;

import com.car.rental.module.system.service.SysConfigService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link SysConfigController}.
 */
@Generated
public class SysConfigController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'sysConfigController'.
   */
  private static BeanInstanceSupplier<SysConfigController> getSysConfigControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SysConfigController>forConstructor(SysConfigService.class)
            .withGenerator((registeredBean, args) -> new SysConfigController(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysConfigController'.
   */
  public static BeanDefinition getSysConfigControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SysConfigController.class);
    beanDefinition.setInstanceSupplier(getSysConfigControllerInstanceSupplier());
    return beanDefinition;
  }
}
