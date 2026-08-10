package com.car.rental.module.marketing.controller;

import com.car.rental.module.marketing.service.CustomerCouponService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CustomerCouponController}.
 */
@Generated
public class CustomerCouponController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'customerCouponController'.
   */
  private static BeanInstanceSupplier<CustomerCouponController> getCustomerCouponControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CustomerCouponController>forConstructor(CustomerCouponService.class)
            .withGenerator((registeredBean, args) -> new CustomerCouponController(args.get(0)));
  }

  /**
   * Get the bean definition for 'customerCouponController'.
   */
  public static BeanDefinition getCustomerCouponControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CustomerCouponController.class);
    beanDefinition.setInstanceSupplier(getCustomerCouponControllerInstanceSupplier());
    return beanDefinition;
  }
}
