package org.mybatis.spring.mapper;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link MapperScannerConfigurer}.
 */
@Generated
public class MapperScannerConfigurer__BeanDefinitions {
  /**
   * Get the bean definition for 'carRentalApplication#MapperScannerRegistrar#0'.
   */
  public static BeanDefinition getCarRentalApplicationMapperScannerRegistrarBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperScannerConfigurer.class);
    beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    beanDefinition.getPropertyValues().addPropertyValue("processPropertyPlaceHolders", true);
    beanDefinition.getPropertyValues().addPropertyValue("basePackage", "com.car.rental.**.mapper");
    beanDefinition.setInstanceSupplier(MapperScannerConfigurer::new);
    return beanDefinition;
  }
}
