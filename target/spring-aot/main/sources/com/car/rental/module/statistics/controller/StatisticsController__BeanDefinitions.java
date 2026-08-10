package com.car.rental.module.statistics.controller;

import com.car.rental.module.statistics.service.StatisticsService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link StatisticsController}.
 */
@Generated
public class StatisticsController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'statisticsController'.
   */
  private static BeanInstanceSupplier<StatisticsController> getStatisticsControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<StatisticsController>forConstructor(StatisticsService.class)
            .withGenerator((registeredBean, args) -> new StatisticsController(args.get(0)));
  }

  /**
   * Get the bean definition for 'statisticsController'.
   */
  public static BeanDefinition getStatisticsControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(StatisticsController.class);
    beanDefinition.setInstanceSupplier(getStatisticsControllerInstanceSupplier());
    return beanDefinition;
  }
}
