package com.car.rental.module.car.service.impl;

import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.module.car.mapper.CarImageMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarImageServiceImpl}.
 */
@Generated
public class CarImageServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carImageServiceImpl'.
   */
  private static BeanInstanceSupplier<CarImageServiceImpl> getCarImageServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarImageServiceImpl>forConstructor(CarImageMapper.class, CarInfoMapper.class)
            .withGenerator((registeredBean, args) -> new CarImageServiceImpl(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'carImageServiceImpl'.
   */
  public static BeanDefinition getCarImageServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarImageServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCarImageServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
