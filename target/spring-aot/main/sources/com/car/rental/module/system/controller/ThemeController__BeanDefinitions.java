package com.car.rental.module.system.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ThemeController}.
 */
@Generated
public class ThemeController__BeanDefinitions {
  /**
   * Get the bean definition for 'themeController'.
   */
  public static BeanDefinition getThemeControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ThemeController.class);
    InstanceSupplier<ThemeController> instanceSupplier = InstanceSupplier.using(ThemeController::new);
    instanceSupplier = instanceSupplier.andThen(ThemeController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
