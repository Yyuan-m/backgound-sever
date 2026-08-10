package com.car.rental.module.system.controller;

import com.car.rental.module.system.service.OperationLogService;
import com.car.rental.module.system.util.OperationLogExporter;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link OperationLogController}.
 */
@Generated
public class OperationLogController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'operationLogController'.
   */
  private static BeanInstanceSupplier<OperationLogController> getOperationLogControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<OperationLogController>forConstructor(OperationLogService.class, OperationLogExporter.class)
            .withGenerator((registeredBean, args) -> new OperationLogController(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'operationLogController'.
   */
  public static BeanDefinition getOperationLogControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OperationLogController.class);
    beanDefinition.setInstanceSupplier(getOperationLogControllerInstanceSupplier());
    return beanDefinition;
  }
}
