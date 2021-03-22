package com.yoshtec.owl.annotations;

import com.yoshtec.owl.Const;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that the Interface or Class is made from an OWL Class with an String Uri. <br>
 * Example:
 * 
 * <pre>
 * {@literal @}OwlClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#PizzaTopping");
 * public class PizzaTopping{
 * &#47;&#47;...
 * }
 * </pre>
 * 
 * @author Jonas von Malottki
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OwlClass {
  /** The URI of the OWL Class. */
  String uri() default Const.DEFAULT_ANNOTATION_STRING;
}
