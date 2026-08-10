package com.car.rental.module.system.controller;

import com.car.rental.module.system.service.SysFileService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link SysFileController}.
 */
@Generated
public class SysFileController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'sysFileController'.
   */
  private static BeanInstanceSupplier<SysFileController> getSysFileControllerInstanceSupplier() {
    return BeanInstanceSupplier.<SysFileController>forConstructor(SysFileService.class)
            .withGenerator((registeredBean, args) -> new SysFileController(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysFileController'.
   */
  public static BeanDefinition getSysFileControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SysFileController.class);
    beanDefinition.setInstanceSupplier(getSysFileControllerInstanceSupplier());
    return beanDefinition;
  }
}
