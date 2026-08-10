package com.car.rental.module.statistics.service.impl;

import com.car.rental.mapper.FinanceRecordMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link FinanceServiceImpl}.
 */
@Generated
public class FinanceServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'financeServiceImpl'.
   */
  private static BeanInstanceSupplier<FinanceServiceImpl> getFinanceServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<FinanceServiceImpl>forConstructor(FinanceRecordMapper.class)
            .withGenerator((registeredBean, args) -> new FinanceServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'financeServiceImpl'.
   */
  public static BeanDefinition getFinanceServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(FinanceServiceImpl.class);
    beanDefinition.setInstanceSupplier(getFinanceServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
