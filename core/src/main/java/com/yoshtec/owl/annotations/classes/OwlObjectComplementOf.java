package com.yoshtec.owl.annotations.classes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlObjectComplementOf {
	/** Complement OWL Class URI's */
	String[] classes();
}
