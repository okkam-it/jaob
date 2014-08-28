package com.yoshtec.owl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Helper Annotation for Annotating DataRanges correctly
 * Example:
 * <pre>
 * //...
 * {@literal @}OwlDataTypes({
 * 	{@literal @}OwlDataType(uri="xsd:string"),
 * 	{@literal @}OwlDataType(uri="xsd:int")
 * 	})
 * private Object value;
 * 
 * </pre>
 * @author malottki
 * @see OwlDataType
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlDataTypes {
	OwlDataType[] value();
}
