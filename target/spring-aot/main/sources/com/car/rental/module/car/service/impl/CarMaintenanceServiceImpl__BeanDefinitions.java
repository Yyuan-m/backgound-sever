package com.car.rental.module.car.service.impl;

import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarMaintenanceServiceImpl}.
 */
@Generated
public class CarMaintenanceServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carMaintenanceServiceImpl'.
   */
  private static BeanInstanceSupplier<CarMaintenanceServiceImpl> getCarMaintenanceServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarMaintenanceServiceImpl>forConstructor(CarMaintenanceMapper.class)
            .withGenerator((registeredBean, args) -> new CarMaintenanceServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'carMaintenanceServiceImpl'.
   */
  public static BeanDefinition getCarMaintenanceServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarMaintenanceServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCarMaintenanceServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
