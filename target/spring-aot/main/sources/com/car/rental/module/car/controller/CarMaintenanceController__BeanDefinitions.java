package com.car.rental.module.car.controller;

import com.car.rental.module.car.service.CarMaintenanceService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarMaintenanceController}.
 */
@Generated
public class CarMaintenanceController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carMaintenanceController'.
   */
  private static BeanInstanceSupplier<CarMaintenanceController> getCarMaintenanceControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarMaintenanceController>forConstructor(CarMaintenanceService.class)
            .withGenerator((registeredBean, args) -> new CarMaintenanceController(args.get(0)));
  }

  /**
   * Get the bean definition for 'carMaintenanceController'.
   */
  public static BeanDefinition getCarMaintenanceControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarMaintenanceController.class);
    beanDefinition.setInstanceSupplier(getCarMaintenanceControllerInstanceSupplier());
    return beanDefinition;
  }
}
