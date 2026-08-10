package com.car.rental.module.statistics.service.impl;

import com.car.rental.module.statistics.mapper.InvoiceMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link InvoiceServiceImpl}.
 */
@Generated
public class InvoiceServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'invoiceServiceImpl'.
   */
  private static BeanInstanceSupplier<InvoiceServiceImpl> getInvoiceServiceImplInstanceSupplier() {
    return BeanInstanceSupplier.<InvoiceServiceImpl>forConstructor(InvoiceMapper.class)
            .withGenerator((registeredBean, args) -> new InvoiceServiceImpl(args.get(0)));
  }

  /**
   * Get the bean definition for 'invoiceServiceImpl'.
   */
  public static BeanDefinition getInvoiceServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InvoiceServiceImpl.class);
    beanDefinition.setInstanceSupplier(getInvoiceServiceImplInstanceSupplier());
    return beanDefinition;
  }
}
