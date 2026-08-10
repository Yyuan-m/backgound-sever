package com.car.rental;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassUtils;

/**
 * Bean definitions for {@link CarRentalApplication}.
 */
@Generated
public class CarRentalApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'carRentalApplication'.
   */
  public static BeanDefinition getCarRentalApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarRentalApplication.class);
    beanDefinition.setTargetType(CarRentalApplication.class);
    ConfigurationClassUtils.initializeConfigurationClass(CarRentalApplication.class);
    beanDefinition.setInstanceSupplier(CarRentalApplication$$SpringCGLIB$$0::new);
    return beanDefinition;
  }
}
