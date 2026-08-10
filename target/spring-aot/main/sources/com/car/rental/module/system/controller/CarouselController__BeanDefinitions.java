package com.car.rental.module.system.controller;

import com.car.rental.module.system.service.CarouselService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarouselController}.
 */
@Generated
public class CarouselController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carouselController'.
   */
  private static BeanInstanceSupplier<CarouselController> getCarouselControllerInstanceSupplier() {
    return BeanInstanceSupplier.<CarouselController>forConstructor(CarouselService.class)
            .withGenerator((registeredBean, args) -> new CarouselController(args.get(0)));
  }

  /**
   * Get the bean definition for 'carouselController'.
   */
  public static BeanDefinition getCarouselControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarouselController.class);
    beanDefinition.setInstanceSupplier(getCarouselControllerInstanceSupplier());
    return beanDefinition;
  }
}
