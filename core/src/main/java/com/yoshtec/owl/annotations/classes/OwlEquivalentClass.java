package com.yoshtec.owl.annotations.classes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <a href="http://www.w3.org/2007/OWL/wiki/Syntax#Equivalent_Classes"> OWL Spec:
 * EquivalentClasses.</a>
 * 
 * @author Jonas von Malottki
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OwlEquivalentClass {
  /**
   * The list of equivalent classes.
   */
  String[] classes();
}
