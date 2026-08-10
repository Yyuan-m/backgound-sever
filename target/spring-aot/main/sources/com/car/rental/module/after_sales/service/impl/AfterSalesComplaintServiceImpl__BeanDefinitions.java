package com.car.rental.module.after_sales.service.impl;

import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AfterSalesComplaintServiceImpl}.
 */
@Generated
public class AfterSalesComplaintServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'afterSalesComplaintServiceImpl'.
   */
  private static BeanInstanceSupplier<AfterSalesComplaintServiceImpl> getAfterSalesComplaintServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<AfterSalesComplaintServiceImpl>forConstructor(AfterSalesComplaintMapper.class)
            .withGenerator((registeredBean, args) -> new AfterSalesComplaintServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'afterSalesComplaintServiceImpl'.
   */
  public static BeanDefinition getAfterSalesComplaintServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AfterSalesComplaintServiceImpl.class);
    beanDefinition.setInstanceSupplier(getAfterSalesComplaintServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
