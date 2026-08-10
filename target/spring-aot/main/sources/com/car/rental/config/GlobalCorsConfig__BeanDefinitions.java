package com.car.rental.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassUtils;
import org.springframework.web.filter.CorsFilter;

/**
 * Bean definitions for {@link GlobalCorsConfig}.
 */
@Generated
public class GlobalCorsConfig__BeanDefinitions {
  /**
   * Get the bean definition for 'globalCorsConfig'.
   */
  public static BeanDefinition getGlobalCorsConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(GlobalCorsConfig.class);
    beanDefinition.setTargetType(GlobalCorsConfig.class);
    ConfigurationClassUtils.initializeConfigurationClass(GlobalCorsConfig.class);
    beanDefinition.setInstanceSupplier(GlobalCorsConfig$$SpringCGLIB$$0::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'corsFilter'.
   */
  private static BeanInstanceSupplier<CorsFilter> getCorsFilterInstanceSupplier() {
    return BeanInstanceSupplier.<CorsFilter>forFactoryMethod(GlobalCorsConfig.class, "corsFilter")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean(GlobalCorsConfig.class).corsFilter());
  }

  /**
   * Get the bean definition for 'corsFilter'.
   */
  public static BeanDefinition getCorsFilterBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CorsFilter.class);
    beanDefinition.setInstanceSupplier(getCorsFilterInstanceSupplier());
    return beanDefinition;
  }
}
