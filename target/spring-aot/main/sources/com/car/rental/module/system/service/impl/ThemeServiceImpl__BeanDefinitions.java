package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ThemeServiceImpl}.
 */
@Generated
public class ThemeServiceImpl__BeanDefinitions {
  /**
   * Get the bean definition for 'themeServiceImpl'.
   */
  public static BeanDefinition getThemeServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ThemeServiceImpl.class);
    InstanceSupplier<ThemeServiceImpl> instanceSupplier = InstanceSupplier.using(ThemeServiceImpl::new);
    instanceSupplier = instanceSupplier.andThen(ThemeServiceImpl__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
