package com.yoshtec.owl.annotations.classes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <a href="http://www.w3.org/2007/OWL/wiki/Syntax#Nominals">OWL Spec: Object one Of</a>.
 * <p>
 * TODO: This can Probably not be solved in Java. Annotations can not contain Objects, providing a
 * list of Individuals is thus difficult. Maybe it is possible to create Enums from that?
 * </p>
 * 
 * @author Jonas von Malottki
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlObjectOneOf {
  /** Uris of Individuals. */
  String[] individualsUri();
}
