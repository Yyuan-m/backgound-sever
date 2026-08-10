package com.car.rental.common.exception;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link GlobalExceptionHandler}.
 */
@Generated
public class GlobalExceptionHandler__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static GlobalExceptionHandler apply(RegisteredBean registeredBean,
      GlobalExceptionHandler instance) {
    AutowiredFieldValueResolver.forRequiredField("activeProfile").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
