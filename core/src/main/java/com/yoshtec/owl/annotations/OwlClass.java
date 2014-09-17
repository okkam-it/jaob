package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yoshtec.owl.Const;

/**
 * Represents that the Interface or Class is made from an OWL Class
 * with an String Uri. 
 * <br>
 * Example:
 * <pre>
 * {@literal @}OwlClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#PizzaTopping");
 * public class PizzaTopping{
 * 	//...
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
	/** The URI of the OWL Class */
	String uri() default Const.DEFAULT_ANNOTATION_STRING;
}
