package com.yoshtec.owl.annotations.classes;

import com.yoshtec.owl.annotations.OwlClass;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <a href="http://www.w3.org/2007/OWL/wiki/Syntax#Subclass_Axioms"> OWL Spec: Subclass Axioms</a>.
 * 
 * @author Jonas von Malottki
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OwlSubclassOf {
  /** List of super-classes. */
  OwlClass[] value();

  // /** URI of the Owl Superclass */
  // String superClass();
  // /** Corresponding Java class */
  // Class<?> javaSuperClass();
}
