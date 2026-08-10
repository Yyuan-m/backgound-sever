package com.car.rental.common.security;

import com.car.rental.module.system.service.OperationLogService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;

/**
 * Bean definitions for {@link OperationLogAspect}.
 */
@Generated
public class OperationLogAspect__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'operationLogAspect'.
   */
  private static BeanInstanceSupplier<OperationLogAspect> getOperationLogAspectInstanceSupplier() {
    return BeanInstanceSupplier.<OperationLogAspect>forConstructor(OperationLogService.class, ApplicationContext.class)
            .withGenerator((registeredBean, args) -> new OperationLogAspect(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'operationLogAspect'.
   */
  public static BeanDefinition getOperationLogAspectBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OperationLogAspect.class);
    beanDefinition.setInstanceSupplier(getOperationLogAspectInstanceSupplier());
    return beanDefinition;
  }
}
