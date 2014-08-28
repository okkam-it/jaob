package com.yoshtec.owl.annotations.classes;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import com.yoshtec.owl.annotations.OwlClass;


@Documented
@Target(value={})
public @interface OwlClassExpression {
    OwlClass owlclass(); 
    OwlObjectIntersectionOf objectIntersectionOf(); 
    OwlObjectUnionOf unionOf(); 
    OwlObjectComplementOf complementOf(); 
    OwlObjectOneOf oneOf(); 
//    ObjectSomeValuesFrom 
//    ObjectAllValuesFrom 
//    ObjectHasValue 
//    ObjectHasSelf 
//    ObjectMinCardinality 
//    ObjectMaxCardinality 
//    ObjectExactCardinality 
//    DataSomeValuesFrom 
//    DataAllValuesFrom 
//    DataHasValue 
//    DataMinCardinality 
//    DataMaxCardinality 
//    DataExactCardinality
}
