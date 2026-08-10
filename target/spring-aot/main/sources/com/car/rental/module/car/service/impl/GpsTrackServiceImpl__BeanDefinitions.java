package com.car.rental.module.car.service.impl;

import com.car.rental.module.car.mapper.GpsTrackMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link GpsTrackServiceImpl}.
 */
@Generated
public class GpsTrackServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'gpsTrackServiceImpl'.
   */
  private static BeanInstanceSupplier<GpsTrackServiceImpl> getGpsTrackServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<GpsTrackServiceImpl>forConstructor(GpsTrackMapper.class)
            .withGenerator((registeredBean, args) -> new GpsTrackServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'gpsTrackServiceImpl'.
   */
  public static BeanDefinition getGpsTrackServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(GpsTrackServiceImpl.class);
    beanDefinition.setInstanceSupplier(getGpsTrackServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
