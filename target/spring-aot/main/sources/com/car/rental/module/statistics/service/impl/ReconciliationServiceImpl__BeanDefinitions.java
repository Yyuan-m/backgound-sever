package com.car.rental.module.statistics.service.impl;

import com.car.rental.module.statistics.mapper.ReconciliationMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ReconciliationServiceImpl}.
 */
@Generated
public class ReconciliationServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'reconciliationServiceImpl'.
   */
  private static BeanInstanceSupplier<ReconciliationServiceImpl> getReconciliationServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<ReconciliationServiceImpl>forConstructor(ReconciliationMapper.class)
            .withGenerator((registeredBean, args) -> new ReconciliationServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'reconciliationServiceImpl'.
   */
  public static BeanDefinition getReconciliationServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ReconciliationServiceImpl.class);
    beanDefinition.setInstanceSupplier(getReconciliationServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
