package com.car.rental.module.system.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RoleController}.
 */
@Generated
public class RoleController__BeanDefinitions {
  /**
   * Get the bean definition for 'roleController'.
   */
  public static BeanDefinition getRoleControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RoleController.class);
    InstanceSupplier<RoleController> instanceSupplier = InstanceSupplier.using(RoleController::new);
    instanceSupplier = instanceSupplier.andThen(RoleController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
