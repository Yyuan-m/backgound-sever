package com.car.rental.module.system.service.impl;

import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.system.mapper.OperationLogMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link OperationLogServiceImpl}.
 */
@Generated
public class OperationLogServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'operationLogServiceImpl'.
   */
  private static BeanInstanceSupplier<OperationLogServiceImpl> getOperationLogServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<OperationLogServiceImpl>forConstructor(OperationLogMapper.class, SysUserMapper.class)
            .withGenerator((registeredBean, args) -> new OperationLogServiceImpl(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'operationLogServiceImpl'.
   */
  public static BeanDefinition getOperationLogServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OperationLogServiceImpl.class);
    beanDefinition.setInstanceSupplier(getOperationLogServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
