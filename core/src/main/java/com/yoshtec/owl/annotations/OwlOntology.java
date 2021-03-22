package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Represents the Definition of the OWL Ontology itself.
 * <p>
 * This is the Ontology which Defines the Classes and Properties for the Individuals created from
 * the Marshalled Objects. Which means that the Marshaller will create a new Ontology that just
 * imports this one.
 * </p>
 * Should be used on a package.
 * 
 * @author Jonas von Malottki
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE})
public @interface OwlOntology {
  /** Definition ontology URI. */
  String uri();
}
