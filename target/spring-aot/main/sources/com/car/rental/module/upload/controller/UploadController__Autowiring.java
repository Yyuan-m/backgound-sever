package com.car.rental.module.upload.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UploadController}.
 */
@Generated
public class UploadController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UploadController apply(RegisteredBean registeredBean, UploadController instance) {
    AutowiredFieldValueResolver.forRequiredField("uploadPath").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
