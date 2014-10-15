package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents OWL Constants
 * <a href="http://www.webont.org/owl/1.1/owl_specification.html#4.1">http://www.webont.org/owl/1.1/owl_specification.html#4.1</a>
 * @author Jonas von Malottki
 *  
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlConstant {
	String	value();
	String 	dataTypeURI();
}
