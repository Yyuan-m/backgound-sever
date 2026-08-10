package com.car.rental.module.customer.controller;

import com.car.rental.module.customer.service.CustomerService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CustomerController}.
 */
@Generated
public class CustomerController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'customerController'.
   */
  private static BeanInstanceSupplier<CustomerController> getCustomerControllerInstanceSupplier() {
    return BeanInstanceSupplier.<CustomerController>forConstructor(CustomerService.class)
            .withGenerator((registeredBean, args) -> new CustomerController(args.get(0)));
  }

  /**
   * Get the bean definition for 'customerController'.
   */
  public static BeanDefinition getCustomerControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CustomerController.class);
    beanDefinition.setInstanceSupplier(getCustomerControllerInstanceSupplier());
    return beanDefinition;
  }
}
