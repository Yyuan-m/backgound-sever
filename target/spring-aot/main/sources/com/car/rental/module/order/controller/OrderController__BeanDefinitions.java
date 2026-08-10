package com.car.rental.module.order.controller;

import com.car.rental.module.order.service.OrderService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link OrderController}.
 */
@Generated
public class OrderController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'orderController'.
   */
  private static BeanInstanceSupplier<OrderController> getOrderControllerInstanceSupplier() {
    return BeanInstanceSupplier.<OrderController>forConstructor(OrderService.class)
            .withGenerator((registeredBean, args) -> new OrderController(args.get(0)));
  }

  /**
   * Get the bean definition for 'orderController'.
   */
  public static BeanDefinition getOrderControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OrderController.class);
    beanDefinition.setInstanceSupplier(getOrderControllerInstanceSupplier());
    return beanDefinition;
  }
}
