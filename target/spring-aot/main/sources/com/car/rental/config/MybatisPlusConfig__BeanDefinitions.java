package com.car.rental.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassUtils;

/**
 * Bean definitions for {@link MybatisPlusConfig}.
 */
@Generated
public class MybatisPlusConfig__BeanDefinitions {
  /**
   * Get the bean definition for 'mybatisPlusConfig'.
   */
  public static BeanDefinition getMybatisPlusConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MybatisPlusConfig.class);
    beanDefinition.setTargetType(MybatisPlusConfig.class);
    ConfigurationClassUtils.initializeConfigurationClass(MybatisPlusConfig.class);
    beanDefinition.setInstanceSupplier(MybatisPlusConfig$$SpringCGLIB$$0::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'mybatisPlusInterceptor'.
   */
  private static BeanInstanceSupplier<MybatisPlusInterceptor> getMybatisPlusInterceptorInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<MybatisPlusInterceptor>forFactoryMethod(MybatisPlusConfig.class, "mybatisPlusInterceptor")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean(MybatisPlusConfig.class).mybatisPlusInterceptor());
  }

  /**
   * Get the bean definition for 'mybatisPlusInterceptor'.
   */
  public static BeanDefinition getMybatisPlusInterceptorBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MybatisPlusInterceptor.class);
    beanDefinition.setInstanceSupplier(getMybatisPlusInterceptorInstanceSupplier());
    return beanDefinition;
  }
}
