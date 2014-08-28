package com.yoshtec.owl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yoshtec.owl.Const;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlIndividual {
    String uri() default Const.DEFAULT_ANNOTATION_STRING;
}
