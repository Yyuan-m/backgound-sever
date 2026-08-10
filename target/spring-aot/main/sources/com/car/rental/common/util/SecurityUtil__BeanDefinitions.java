package com.car.rental.common.util;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link SecurityUtil}.
 */
@Generated
public class SecurityUtil__BeanDefinitions {
  /**
   * Get the bean definition for 'securityUtil'.
   */
  public static BeanDefinition getSecurityUtilBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SecurityUtil.class);
    beanDefinition.setInstanceSupplier(SecurityUtil::new);
    return beanDefinition;
  }
}
