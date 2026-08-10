package com.car.rental.module.system.service.impl;

import com.car.rental.module.system.mapper.SysDictDataMapper;
import com.car.rental.module.system.mapper.SysDictTypeMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link DictServiceImpl}.
 */
@Generated
public class DictServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'dictServiceImpl'.
   */
  private static BeanInstanceSupplier<DictServiceImpl> getDictServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<DictServiceImpl>forConstructor(SysDictTypeMapper.class, SysDictDataMapper.class)
            .withGenerator((registeredBean, args) -> new DictServiceImpl(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'dictServiceImpl'.
   */
  public static BeanDefinition getDictServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DictServiceImpl.class);
    beanDefinition.setInstanceSupplier(getDictServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
