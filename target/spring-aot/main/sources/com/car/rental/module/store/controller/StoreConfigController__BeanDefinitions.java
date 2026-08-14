package com.car.rental.module.store.controller;

import com.car.rental.module.store.service.StoreConfigService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link StoreConfigController}.
 */
@Generated
public class StoreConfigController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'storeConfigController'.
   */
  private static BeanInstanceSupplier<StoreConfigController> getStoreConfigControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<StoreConfigController>forConstructor(StoreConfigService.class)
            .withGenerator((registeredBean, args) -> new StoreConfigController(args.get(0)));
  }

  /**
   * Get the bean definition for 'storeConfigController'.
   */
  public static BeanDefinition getStoreConfigControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(StoreConfigController.class);
    beanDefinition.setInstanceSupplier(getStoreConfigControllerInstanceSupplier());
    return beanDefinition;
  }
}
