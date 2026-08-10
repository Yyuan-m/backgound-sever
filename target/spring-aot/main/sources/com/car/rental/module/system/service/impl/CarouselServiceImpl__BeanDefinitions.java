package com.car.rental.module.system.service.impl;

import com.car.rental.module.system.mapper.CarouselMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarouselServiceImpl}.
 */
@Generated
public class CarouselServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carouselServiceImpl'.
   */
  private static BeanInstanceSupplier<CarouselServiceImpl> getCarouselServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarouselServiceImpl>forConstructor(CarouselMapper.class)
            .withGenerator((registeredBean, args) -> new CarouselServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'carouselServiceImpl'.
   */
  public static BeanDefinition getCarouselServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarouselServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCarouselServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
