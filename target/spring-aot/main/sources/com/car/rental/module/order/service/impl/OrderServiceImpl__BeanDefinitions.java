package com.car.rental.module.order.service.impl;

import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.module.marketing.service.CustomerCouponService;
import com.car.rental.module.statistics.mapper.InvoiceMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link OrderServiceImpl}.
 */
@Generated
public class OrderServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'orderServiceImpl'.
   */
  private static BeanInstanceSupplier<OrderServiceImpl> getOrderServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<OrderServiceImpl>forConstructor(CustomerOrderMapper.class, CustomerCouponService.class, InvoiceMapper.class, FinanceRecordMapper.class, CarInfoMapper.class)
            .withGenerator((registeredBean, args) -> new OrderServiceImpl(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4)));
  }

  /**
   * Get the bean definition for 'orderServiceImpl'.
   */
  public static BeanDefinition getOrderServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OrderServiceImpl.class);
    beanDefinition.setInstanceSupplier(getOrderServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
