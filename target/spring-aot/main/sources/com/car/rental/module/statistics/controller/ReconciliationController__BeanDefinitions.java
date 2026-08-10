package com.car.rental.module.statistics.controller;

import com.car.rental.module.statistics.service.ReconciliationService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ReconciliationController}.
 */
@Generated
public class ReconciliationController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'reconciliationController'.
   */
  private static BeanInstanceSupplier<ReconciliationController> getReconciliationControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<ReconciliationController>forConstructor(ReconciliationService.class)
            .withGenerator((registeredBean, args) -> new ReconciliationController(args.get(0)));
  }

  /**
   * Get the bean definition for 'reconciliationController'.
   */
  public static BeanDefinition getReconciliationControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ReconciliationController.class);
    beanDefinition.setInstanceSupplier(getReconciliationControllerInstanceSupplier());
    return beanDefinition;
  }
}
