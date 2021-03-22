package com.yoshtec.owl.annotations.classes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OwlObjectIntersectionOf {
  /**
   * The list of ontology classes.
   */
  String[] classes();

  /**
   * The list of Java classes.
   */
  Class<?>[] javaclasses() default {};
}
