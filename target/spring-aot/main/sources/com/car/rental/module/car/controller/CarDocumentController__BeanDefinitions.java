package com.car.rental.module.car.controller;

import com.car.rental.module.car.service.CarDocumentService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarDocumentController}.
 */
@Generated
public class CarDocumentController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carDocumentController'.
   */
  private static BeanInstanceSupplier<CarDocumentController> getCarDocumentControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarDocumentController>forConstructor(CarDocumentService.class)
            .withGenerator((registeredBean, args) -> new CarDocumentController(args.get(0)));
  }

  /**
   * Get the bean definition for 'carDocumentController'.
   */
  public static BeanDefinition getCarDocumentControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarDocumentController.class);
    beanDefinition.setInstanceSupplier(getCarDocumentControllerInstanceSupplier());
    return beanDefinition;
  }
}
