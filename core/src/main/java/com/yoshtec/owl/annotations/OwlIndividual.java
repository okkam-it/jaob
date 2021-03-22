package com.yoshtec.owl.annotations;

import com.yoshtec.owl.Const;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlIndividual {
  /**
   * Returns the URI of this individual.
   * 
   * @return the URI of this individual
   */
  String uri() default Const.DEFAULT_ANNOTATION_STRING;
}
