package com.car.rental.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.ConfigurationClassUtils;

/**
 * Bean definitions for {@link JacksonConfig}.
 */
@Generated
public class JacksonConfig__BeanDefinitions {
  /**
   * Get the bean definition for 'jacksonConfig'.
   */
  public static BeanDefinition getJacksonConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(JacksonConfig.class);
    beanDefinition.setTargetType(JacksonConfig.class);
    ConfigurationClassUtils.initializeConfigurationClass(JacksonConfig.class);
    beanDefinition.setInstanceSupplier(JacksonConfig$$SpringCGLIB$$0::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'jacksonCustomizer'.
   */
  private static BeanInstanceSupplier<Jackson2ObjectMapperBuilderCustomizer> getJacksonCustomizerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<Jackson2ObjectMapperBuilderCustomizer>forFactoryMethod(JacksonConfig.class, "jacksonCustomizer")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean(JacksonConfig.class).jacksonCustomizer());
  }

  /**
   * Get the bean definition for 'jacksonCustomizer'.
   */
  public static BeanDefinition getJacksonCustomizerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(Jackson2ObjectMapperBuilderCustomizer.class);
    beanDefinition.setInstanceSupplier(getJacksonCustomizerInstanceSupplier());
    return beanDefinition;
  }
}
