package com.car.rental.module.statistics.service.impl;

import com.car.rental.module.statistics.mapper.CostRecordMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CostServiceImpl}.
 */
@Generated
public class CostServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'costServiceImpl'.
   */
  private static BeanInstanceSupplier<CostServiceImpl> getCostServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<CostServiceImpl>forConstructor(CostRecordMapper.class)
            .withGenerator((registeredBean, args) -> new CostServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'costServiceImpl'.
   */
  public static BeanDefinition getCostServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CostServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCostServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
