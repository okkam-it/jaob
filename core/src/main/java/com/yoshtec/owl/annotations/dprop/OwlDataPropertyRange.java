package com.yoshtec.owl.annotations.dprop;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlDataPropertyRange {

  /**
   * The data-property range.
   */
  String range();
}
