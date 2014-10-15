package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.yoshtec.owl.Const;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlDataProperty {
	/** the Uri of the OWL Data Property */
	String uri() default Const.DEFAULT_ANNOTATION_STRING;
}