package com.car.rental.module.system.controller;

import com.car.rental.module.system.service.AnnouncementService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AnnouncementController}.
 */
@Generated
public class AnnouncementController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'announcementController'.
   */
  private static BeanInstanceSupplier<AnnouncementController> getAnnouncementControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<AnnouncementController>forConstructor(AnnouncementService.class)
            .withGenerator((registeredBean, args) -> new AnnouncementController(args.get(0)));
  }

  /**
   * Get the bean definition for 'announcementController'.
   */
  public static BeanDefinition getAnnouncementControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AnnouncementController.class);
    beanDefinition.setInstanceSupplier(getAnnouncementControllerInstanceSupplier());
    return beanDefinition;
  }
}
