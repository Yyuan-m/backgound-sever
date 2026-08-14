package com.car.rental.module.marketing.service.impl;

import com.car.rental.common.util.SecurityUtil;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.marketing.mapper.CouponCarMapper;
import com.car.rental.module.marketing.mapper.CouponMapper;
import com.car.rental.module.marketing.mapper.MemberCouponMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CouponServiceImpl}.
 */
@Generated
public class CouponServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'couponServiceImpl'.
   */
  private static BeanInstanceSupplier<CouponServiceImpl> getCouponServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<CouponServiceImpl>forConstructor(CouponMapper.class, CouponCarMapper.class, MemberCouponMapper.class, CustomerOrderMapper.class, SecurityUtil.class)
            .withGenerator((registeredBean, args) -> new CouponServiceImpl(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4)));
  }

  /**
   * Get the bean definition for 'couponServiceImpl'.
   */
  public static BeanDefinition getCouponServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CouponServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCouponServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
