package com.car.rental.module.marketing.service.impl;

import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.marketing.mapper.CouponCarMapper;
import com.car.rental.module.marketing.mapper.CouponMapper;
import com.car.rental.module.marketing.mapper.MemberCouponMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CustomerCouponServiceImpl}.
 */
@Generated
public class CustomerCouponServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'customerCouponServiceImpl'.
   */
  private static BeanInstanceSupplier<CustomerCouponServiceImpl> getCustomerCouponServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CustomerCouponServiceImpl>forConstructor(CouponMapper.class, MemberCouponMapper.class, CouponCarMapper.class, CustomerOrderMapper.class)
            .withGenerator((registeredBean, args) -> new CustomerCouponServiceImpl(args.get(0), args.get(1), args.get(2), args.get(3)));
  }

  /**
   * Get the bean definition for 'customerCouponServiceImpl'.
   */
  public static BeanDefinition getCustomerCouponServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CustomerCouponServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCustomerCouponServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
