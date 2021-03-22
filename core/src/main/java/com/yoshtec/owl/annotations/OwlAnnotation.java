package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlAnnotation {
  /**
   * The URI.
   */
  String uri();

  /**
   * The content.
   */
  String content();

  /**
   * The data type URI.
   */
  String dataTypeUri() default "";
}
