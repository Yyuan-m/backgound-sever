package com.car.rental.module.marketing.controller;

import com.car.rental.module.marketing.service.CouponService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CouponController}.
 */
@Generated
public class CouponController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'couponController'.
   */
  private static BeanInstanceSupplier<CouponController> getCouponControllerInstanceSupplier() {
    return BeanInstanceSupplier.<CouponController>forConstructor(CouponService.class)
            .withGenerator((registeredBean, args) -> new CouponController(args.get(0)));
  }

  /**
   * Get the bean definition for 'couponController'.
   */
  public static BeanDefinition getCouponControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CouponController.class);
    beanDefinition.setInstanceSupplier(getCouponControllerInstanceSupplier());
    return beanDefinition;
  }
}
