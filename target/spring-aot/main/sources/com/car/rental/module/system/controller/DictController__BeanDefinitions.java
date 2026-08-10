package com.car.rental.module.system.controller;

import com.car.rental.module.system.service.DictService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link DictController}.
 */
@Generated
public class DictController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'dictController'.
   */
  private static BeanInstanceSupplier<DictController> getDictControllerInstanceSupplier() {
    return BeanInstanceSupplier.<DictController>forConstructor(DictService.class)
            .withGenerator((registeredBean, args) -> new DictController(args.get(0)));
  }

  /**
   * Get the bean definition for 'dictController'.
   */
  public static BeanDefinition getDictControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(DictController.class);
    beanDefinition.setInstanceSupplier(getDictControllerInstanceSupplier());
    return beanDefinition;
  }
}
