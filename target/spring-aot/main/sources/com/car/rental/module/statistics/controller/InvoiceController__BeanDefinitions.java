package com.car.rental.module.statistics.controller;

import com.car.rental.module.statistics.service.InvoiceService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link InvoiceController}.
 */
@Generated
public class InvoiceController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'invoiceController'.
   */
  private static BeanInstanceSupplier<InvoiceController> getInvoiceControllerInstanceSupplier() {
    return BeanInstanceSupplier.<InvoiceController>forConstructor(InvoiceService.class)
            .withGenerator((registeredBean, args) -> new InvoiceController(args.get(0)));
  }

  /**
   * Get the bean definition for 'invoiceController'.
   */
  public static BeanDefinition getInvoiceControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InvoiceController.class);
    beanDefinition.setInstanceSupplier(getInvoiceControllerInstanceSupplier());
    return beanDefinition;
  }
}
