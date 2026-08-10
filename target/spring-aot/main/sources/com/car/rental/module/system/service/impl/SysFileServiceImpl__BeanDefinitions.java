package com.car.rental.module.system.service.impl;

import com.car.rental.module.system.mapper.SysFileMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link SysFileServiceImpl}.
 */
@Generated
public class SysFileServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'sysFileServiceImpl'.
   */
  private static BeanInstanceSupplier<SysFileServiceImpl> getSysFileServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<SysFileServiceImpl>forConstructor(SysFileMapper.class)
            .withGenerator((registeredBean, args) -> new SysFileServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysFileServiceImpl'.
   */
  public static BeanDefinition getSysFileServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SysFileServiceImpl.class);
    InstanceSupplier<SysFileServiceImpl> instanceSupplier = getSysFileServiceImplInstanceSupplier();
    instanceSupplier = instanceSupplier.andThen(SysFileServiceImpl__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
