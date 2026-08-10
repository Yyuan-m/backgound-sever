package com.car.rental.module.upload.controller;

import com.car.rental.common.util.SecurityUtil;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.module.system.service.SysFileService;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UploadController}.
 */
@Generated
public class UploadController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'uploadController'.
   */
  private static BeanInstanceSupplier<UploadController> getUploadControllerInstanceSupplier() {
    return BeanInstanceSupplier.<UploadController>forConstructor(SysFileService.class, SecurityUtil.class, SysUserMapper.class)
            .withGenerator((registeredBean, args) -> new UploadController(args.get(0), args.get(1), args.get(2)));
  }

  /**
   * Get the bean definition for 'uploadController'.
   */
  public static BeanDefinition getUploadControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UploadController.class);
    InstanceSupplier<UploadController> instanceSupplier = getUploadControllerInstanceSupplier();
    instanceSupplier = instanceSupplier.andThen(UploadController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
