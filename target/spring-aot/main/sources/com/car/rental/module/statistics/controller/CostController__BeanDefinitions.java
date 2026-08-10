package com.car.rental.module.statistics.controller;

import com.car.rental.module.statistics.service.CostService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CostController}.
 */
@Generated
public class CostController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'costController'.
   */
  private static BeanInstanceSupplier<CostController> getCostControllerInstanceSupplier() {
    return BeanInstanceSupplier.<CostController>forConstructor(CostService.class)
            .withGenerator((registeredBean, args) -> new CostController(args.get(0)));
  }

  /**
   * Get the bean definition for 'costController'.
   */
  public static BeanDefinition getCostControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CostController.class);
    beanDefinition.setInstanceSupplier(getCostControllerInstanceSupplier());
    return beanDefinition;
  }
}
