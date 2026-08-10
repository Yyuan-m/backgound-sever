package com.car.rental.module.auth.service.impl;

import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.auth.service.AuthService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ProfileServiceImpl}.
 */
@Generated
public class ProfileServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'profileServiceImpl'.
   */
  private static BeanInstanceSupplier<ProfileServiceImpl> getProfileServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<ProfileServiceImpl>forConstructor(SysUserMapper.class, AuthService.class)
            .withGenerator((registeredBean, args) -> new ProfileServiceImpl(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'profileServiceImpl'.
   */
  public static BeanDefinition getProfileServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ProfileServiceImpl.class);
    beanDefinition.setInstanceSupplier(getProfileServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
