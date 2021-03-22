package com.yoshtec.owl.annotations.oprop;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlSubObjectPropertyOf {
  /**
   * The super property of this object property.
   */
  String superObjectProperty();
}
