package com.car.rental.module.customer.service.impl;

import com.car.rental.mapper.CustomerInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CustomerServiceImpl}.
 */
@Generated
public class CustomerServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'customerServiceImpl'.
   */
  private static BeanInstanceSupplier<CustomerServiceImpl> getCustomerServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CustomerServiceImpl>forConstructor(CustomerInfoMapper.class, CustomerOrderMapper.class)
            .withGenerator((registeredBean, args) -> new CustomerServiceImpl(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'customerServiceImpl'.
   */
  public static BeanDefinition getCustomerServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CustomerServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCustomerServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
