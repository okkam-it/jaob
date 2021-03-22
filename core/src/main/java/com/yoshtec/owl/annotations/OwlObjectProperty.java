package com.yoshtec.owl.annotations;

import com.yoshtec.owl.Const;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlObjectProperty {
  /** OWL Object Property URI. */
  String uri() default Const.DEFAULT_ANNOTATION_STRING;
}
