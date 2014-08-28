package com.yoshtec.owl.annotations.classes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <a href="http://www.w3.org/2007/OWL/wiki/Syntax#Union">
 * OWL Spec: ObjectUnionOf</a>
 * 
 * TODO: this could be solved in the codegen via creating an Interface 
 * with an name that represents this union, in respect that the interface
 * extends the other classes.
 * 
 * @author Jonas von Malottki
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OwlObjectUnionOf {
	/** Class URIs */
	String[] classes();
}
