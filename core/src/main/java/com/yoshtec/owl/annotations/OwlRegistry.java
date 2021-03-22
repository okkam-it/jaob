package com.yoshtec.owl.annotations;

import com.yoshtec.owl.marshall.UnMarshaller;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation is to mark Object Factories for Classes created 
 * from OWL Ontologies. For instance via the Code generator. <br> 
 * It can be used to register Classes for unmarshalling at 
 * the {@link UnMarshaller}
 * 
 * @author Jonas von Malottki
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OwlRegistry {

}
