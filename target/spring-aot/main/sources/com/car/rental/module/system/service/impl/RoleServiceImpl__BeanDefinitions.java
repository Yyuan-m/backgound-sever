package com.car.rental.module.system.service.impl;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RoleServiceImpl}.
 */
@Generated
public class RoleServiceImpl__BeanDefinitions {
  /**
   * Get the bean definition for 'roleServiceImpl'.
   */
  public static BeanDefinition getRoleServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RoleServiceImpl.class);
    InstanceSupplier<RoleServiceImpl> instanceSupplier = InstanceSupplier.using(RoleServiceImpl::new);
    instanceSupplier = instanceSupplier.andThen(RoleServiceImpl__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
