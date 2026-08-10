package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link MenuServiceImpl}.
 */
@Generated
public class MenuServiceImpl__BeanDefinitions {
  /**
   * Get the bean definition for 'menuServiceImpl'.
   */
  public static BeanDefinition getMenuServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MenuServiceImpl.class);
    InstanceSupplier<MenuServiceImpl> instanceSupplier = InstanceSupplier.using(MenuServiceImpl::new);
    instanceSupplier = instanceSupplier.andThen(MenuServiceImpl__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
