package com.yoshtec.owl.annotations.classes;

import com.yoshtec.owl.annotations.OwlClass;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;


@Documented
@Target(value = {})
public @interface OwlClassExpression {
  /**
   * Returns the OWL class.
   */
  OwlClass owlclass();

  /**
   * Returns the OWL object intersection of.
   */
  OwlObjectIntersectionOf objectIntersectionOf();

  /**
   * Returns the OWL union of.
   */
  OwlObjectUnionOf unionOf();


  /**
   * Returns the OWL complement of.
   */
  OwlObjectComplementOf complementOf();

  /**
   * Returns the OWL one of.
   */
  OwlObjectOneOf oneOf();
  // ObjectSomeValuesFrom
  // ObjectAllValuesFrom
  // ObjectHasValue
  // ObjectHasSelf
  // ObjectMinCardinality
  // ObjectMaxCardinality
  // ObjectExactCardinality
  // DataSomeValuesFrom
  // DataAllValuesFrom
  // DataHasValue
  // DataMinCardinality
  // DataMaxCardinality
  // DataExactCardinality
}
