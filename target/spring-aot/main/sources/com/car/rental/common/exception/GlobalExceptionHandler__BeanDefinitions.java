package com.car.rental.common.exception;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link GlobalExceptionHandler}.
 */
@Generated
public class GlobalExceptionHandler__BeanDefinitions {
  /**
   * Get the bean definition for 'globalExceptionHandler'.
   */
  public static BeanDefinition getGlobalExceptionHandlerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(GlobalExceptionHandler.class);
    InstanceSupplier<GlobalExceptionHandler> instanceSupplier = InstanceSupplier.using(GlobalExceptionHandler::new);
    instanceSupplier = instanceSupplier.andThen(GlobalExceptionHandler__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
