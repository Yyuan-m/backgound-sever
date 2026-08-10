package com.car.rental.module.statistics.controller;

import com.car.rental.module.statistics.service.FinanceService;
import com.car.rental.module.statistics.service.FinanceStatsService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link FinanceController}.
 */
@Generated
public class FinanceController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'financeController'.
   */
  private static BeanInstanceSupplier<FinanceController> getFinanceControllerInstanceSupplier() {
    return BeanInstanceSupplier.<FinanceController>forConstructor(FinanceService.class, FinanceStatsService.class)
            .withGenerator((registeredBean, args) -> new FinanceController(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'financeController'.
   */
  public static BeanDefinition getFinanceControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(FinanceController.class);
    beanDefinition.setInstanceSupplier(getFinanceControllerInstanceSupplier());
    return beanDefinition;
  }
}
