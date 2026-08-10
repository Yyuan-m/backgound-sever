package com.car.rental.module.system.service.impl;

import com.car.rental.mapper.SysConfigMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link SysConfigServiceImpl}.
 */
@Generated
public class SysConfigServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'sysConfigServiceImpl'.
   */
  private static BeanInstanceSupplier<SysConfigServiceImpl> getSysConfigServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SysConfigServiceImpl>forConstructor(SysConfigMapper.class)
            .withGenerator((registeredBean, args) -> new SysConfigServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysConfigServiceImpl'.
   */
  public static BeanDefinition getSysConfigServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SysConfigServiceImpl.class);
    beanDefinition.setInstanceSupplier(getSysConfigServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
