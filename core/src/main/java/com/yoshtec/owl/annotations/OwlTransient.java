package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that a Field shall not be marshalled or unmarshalled.
 * 
 * This can also happen by the Java <code>transient</code> keyword for
 * fields. But sometimes it is desired, that a field may be serialized 
 * by some other serialization technique but not by JAOB.
 * 
 * @author Jonas von Malottki
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD})
public @interface OwlTransient {

}
