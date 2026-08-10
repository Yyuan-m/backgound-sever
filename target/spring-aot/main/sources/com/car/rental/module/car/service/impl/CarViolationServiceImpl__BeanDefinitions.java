package com.car.rental.module.car.service.impl;

import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.module.car.mapper.CarViolationMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarViolationServiceImpl}.
 */
@Generated
public class CarViolationServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carViolationServiceImpl'.
   */
  private static BeanInstanceSupplier<CarViolationServiceImpl> getCarViolationServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarViolationServiceImpl>forConstructor(CarViolationMapper.class, CustomerOrderMapper.class)
            .withGenerator((registeredBean, args) -> new CarViolationServiceImpl(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'carViolationServiceImpl'.
   */
  public static BeanDefinition getCarViolationServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarViolationServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCarViolationServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
