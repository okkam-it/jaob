package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Allows to set the name an Lexical value of an Enum constant in 
 * the OWL Model to a different value than the Java Enum name. 
 * <br>
 * 
 * <b>Example:</b> <br>
 * <code><pre> 
 * {@literal @}OwlClass
 * public enum Gender {
 *      {@literal @}OwlEnumValue("Male")
 *      MALE,
 *      FEMALE
 * }
 * </pre> </code>
 * <br>
 * In this example the OWL enumerated class "Gender" will have to individuals.
 * Namely "Male" and "FEMALE"
 *  
 * 
 * 
 * @author Jonas von Malottki
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OwlEnumValue {
    String value();
}
