package com.car.rental.module.car.controller;

import com.car.rental.module.car.service.CarViolationService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarViolationController}.
 */
@Generated
public class CarViolationController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carViolationController'.
   */
  private static BeanInstanceSupplier<CarViolationController> getCarViolationControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarViolationController>forConstructor(CarViolationService.class)
            .withGenerator((registeredBean, args) -> new CarViolationController(args.get(0)));
  }

  /**
   * Get the bean definition for 'carViolationController'.
   */
  public static BeanDefinition getCarViolationControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarViolationController.class);
    beanDefinition.setInstanceSupplier(getCarViolationControllerInstanceSupplier());
    return beanDefinition;
  }
}
