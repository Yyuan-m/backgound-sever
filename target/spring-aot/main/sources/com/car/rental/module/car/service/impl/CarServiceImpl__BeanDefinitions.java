package com.car.rental.module.car.service.impl;

import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.module.car.mapper.CarConfigMapper;
import com.car.rental.module.car.service.CarImageService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarServiceImpl}.
 */
@Generated
public class CarServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carServiceImpl'.
   */
  private static BeanInstanceSupplier<CarServiceImpl> getCarServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<CarServiceImpl>forConstructor(CarInfoMapper.class, CarConfigMapper.class, CarImageService.class)
            .withGenerator((registeredBean, args) -> new CarServiceImpl(args.get(0), args.get(1), args.get(2)));
  }

  /**
   * Get the bean definition for 'carServiceImpl'.
   */
  public static BeanDefinition getCarServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCarServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
