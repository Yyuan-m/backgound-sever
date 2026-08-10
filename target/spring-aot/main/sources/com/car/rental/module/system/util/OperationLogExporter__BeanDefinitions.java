package com.car.rental.module.system.util;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link OperationLogExporter}.
 */
@Generated
public class OperationLogExporter__BeanDefinitions {
  /**
   * Get the bean definition for 'operationLogExporter'.
   */
  public static BeanDefinition getOperationLogExporterBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OperationLogExporter.class);
    beanDefinition.setInstanceSupplier(OperationLogExporter::new);
    return beanDefinition;
  }
}
