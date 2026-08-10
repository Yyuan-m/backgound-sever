package com.car.rental.module.car.controller;

import com.car.rental.module.car.service.CarImageService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarImageController}.
 */
@Generated
public class CarImageController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carImageController'.
   */
  private static BeanInstanceSupplier<CarImageController> getCarImageControllerInstanceSupplier() {
    return BeanInstanceSupplier.<CarImageController>forConstructor(CarImageService.class)
            .withGenerator((registeredBean, args) -> new CarImageController(args.get(0)));
  }

  /**
   * Get the bean definition for 'carImageController'.
   */
  public static BeanDefinition getCarImageControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarImageController.class);
    beanDefinition.setInstanceSupplier(getCarImageControllerInstanceSupplier());
    return beanDefinition;
  }
}
