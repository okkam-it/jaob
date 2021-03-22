package com.yoshtec.owl.annotations;

import com.yoshtec.owl.PropertyAccessType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OwlAccessorType.
 * 
 * @author Jonas von Malottki
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OwlAccessorType {
  /**
   * Returns the value.
   */
  PropertyAccessType value();
}
