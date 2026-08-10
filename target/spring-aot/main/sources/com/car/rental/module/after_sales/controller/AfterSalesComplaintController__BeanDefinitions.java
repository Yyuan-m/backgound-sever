package com.car.rental.module.after_sales.controller;

import com.car.rental.module.after_sales.service.AfterSalesComplaintService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AfterSalesComplaintController}.
 */
@Generated
public class AfterSalesComplaintController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'afterSalesComplaintController'.
   */
  private static BeanInstanceSupplier<AfterSalesComplaintController> getAfterSalesComplaintControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<AfterSalesComplaintController>forConstructor(AfterSalesComplaintService.class)
            .withGenerator((registeredBean, args) -> new AfterSalesComplaintController(args.get(0)));
  }

  /**
   * Get the bean definition for 'afterSalesComplaintController'.
   */
  public static BeanDefinition getAfterSalesComplaintControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AfterSalesComplaintController.class);
    beanDefinition.setInstanceSupplier(getAfterSalesComplaintControllerInstanceSupplier());
    return beanDefinition;
  }
}
