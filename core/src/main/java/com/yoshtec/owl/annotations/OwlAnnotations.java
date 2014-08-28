package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Is used to annotate Java classes with owl Constructs:
 * </br>
 * Example:
 * <pre>
 * {@literal @}OwlAnnotations({
 * 		{@literal @}OwlAnnotation(TODO),
 * 		{@literal @}OwlAnnotation(TODO)
 * })
 * public class CheseTopping extends PizzaTopping{
 *  // methods
 * }
 * </pre>
 * 
 * @author Jonas von Malottki
 * @see OwlAnnotation
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlAnnotations {
	OwlAnnotation[] value();
}
