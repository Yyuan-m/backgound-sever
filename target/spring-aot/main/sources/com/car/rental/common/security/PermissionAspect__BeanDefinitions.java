package com.car.rental.common.security;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link PermissionAspect}.
 */
@Generated
public class PermissionAspect__BeanDefinitions {
  /**
   * Get the bean definition for 'permissionAspect'.
   */
  public static BeanDefinition getPermissionAspectBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(PermissionAspect.class);
    beanDefinition.setInstanceSupplier(PermissionAspect::new);
    return beanDefinition;
  }
}
