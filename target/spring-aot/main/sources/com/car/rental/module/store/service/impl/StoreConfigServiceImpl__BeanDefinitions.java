package com.car.rental.module.store.service.impl;

import com.car.rental.module.store.mapper.CityMapper;
import com.car.rental.module.store.mapper.StoreMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link StoreConfigServiceImpl}.
 */
@Generated
public class StoreConfigServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'storeConfigServiceImpl'.
   */
  private static BeanInstanceSupplier<StoreConfigServiceImpl> getStoreConfigServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<StoreConfigServiceImpl>forConstructor(CityMapper.class, StoreMapper.class)
            .withGenerator((registeredBean, args) -> new StoreConfigServiceImpl(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'storeConfigServiceImpl'.
   */
  public static BeanDefinition getStoreConfigServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(StoreConfigServiceImpl.class);
    beanDefinition.setInstanceSupplier(getStoreConfigServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
