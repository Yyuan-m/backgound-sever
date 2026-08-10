package com.car.rental.module.system.service.impl;

import com.car.rental.mapper.AnnouncementMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AnnouncementServiceImpl}.
 */
@Generated
public class AnnouncementServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'announcementServiceImpl'.
   */
  private static BeanInstanceSupplier<AnnouncementServiceImpl> getAnnouncementServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<AnnouncementServiceImpl>forConstructor(AnnouncementMapper.class)
            .withGenerator((registeredBean, args) -> new AnnouncementServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'announcementServiceImpl'.
   */
  public static BeanDefinition getAnnouncementServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AnnouncementServiceImpl.class);
    beanDefinition.setInstanceSupplier(getAnnouncementServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
