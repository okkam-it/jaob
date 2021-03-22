package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 * public class Food{
 *       {@literal @}OwlDataType("http://www.co-ode.org/ontologies/pizza/pizza.owl#Food")
 *       private List&lt;Food&gt; hasTopping = null;
 *       {@literal @}OwlDataProperty("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasIngredient")
 *       {@literal @}OwlDataType("http://www.w3.org/2001/XMLSchema#String")
 *       {@literal @}OwlFunctionalDataProperty
 *       private String hasName = "Name";
 * }
 * </pre>
 * 
 * @author Jonas von Malottki
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlDataType {
  /** OWL data type URI. */
  String uri();
}
