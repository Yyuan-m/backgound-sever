package com.car.rental.module.car.service.impl;

import com.car.rental.module.car.mapper.CarDocumentMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CarDocumentServiceImpl}.
 */
@Generated
public class CarDocumentServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'carDocumentServiceImpl'.
   */
  private static BeanInstanceSupplier<CarDocumentServiceImpl> getCarDocumentServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CarDocumentServiceImpl>forConstructor(CarDocumentMapper.class)
            .withGenerator((registeredBean, args) -> new CarDocumentServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'carDocumentServiceImpl'.
   */
  public static BeanDefinition getCarDocumentServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CarDocumentServiceImpl.class);
    beanDefinition.setInstanceSupplier(getCarDocumentServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
