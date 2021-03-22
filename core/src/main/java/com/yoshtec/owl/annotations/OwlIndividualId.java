package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the Marshaller with the Information how to build the individual URI.
 * <p>
 * Before creating the Individual the Marshaller will look for this Annotation on a Field or Method.
 * The Method may not have any Arguments and has to return a value (String is usually the best
 * choice).
 * </p>
 * Consider the following Examples:
 * <h5>Example 1</h5>
 * 
 * <pre>
 * {@literal @}OwlClass(uri="some:uri#MyClass")
 * public class MyClass{
 * 
 *  {@literal @}OwlIndividualId
 *  private String id;
 *
 *  &#47;&#47;... other stuff
 * }
 * </pre>
 * Here the marshaller will read the value from the field {@code id} and will build an individual
 * URI like {@code ontologyUri + "#" + id}.
 * 
 * <h5>Example 2</h5>
 * 
 * <pre>
 * {@literal @}OwlClass(uri="some:uri#MyClass")
 * public class MyClass{
 * 
 *  {@literal @}OwlIndividualId
 *  private Object getInfo(){
 *
 *  }
 *  &#47;&#47;... other stuff
 *}
 * </pre>
 * Here the marshaller will call the Method {@code getInfo()} and will create an Individual with the
 * URI {@code ontologyUri + "#" + getInfo().toString()}.
 * 
 * @author Jonas von Malottki
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface OwlIndividualId {

}
