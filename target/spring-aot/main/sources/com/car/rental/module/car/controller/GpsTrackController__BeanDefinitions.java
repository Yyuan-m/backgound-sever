package com.car.rental.module.car.controller;

import com.car.rental.module.car.service.GpsTrackService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link GpsTrackController}.
 */
@Generated
public class GpsTrackController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'gpsTrackController'.
   */
  private static BeanInstanceSupplier<GpsTrackController> getGpsTrackControllerInstanceSupplier() {
    return BeanInstanceSupplier.<GpsTrackController>forConstructor(GpsTrackService.class)
            .withGenerator((registeredBean, args) -> new GpsTrackController(args.get(0)));
  }

  /**
   * Get the bean definition for 'gpsTrackController'.
   */
  public static BeanDefinition getGpsTrackControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(GpsTrackController.class);
    beanDefinition.setInstanceSupplier(getGpsTrackControllerInstanceSupplier());
    return beanDefinition;
  }
}
