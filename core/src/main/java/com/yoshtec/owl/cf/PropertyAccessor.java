package com.yoshtec.owl.cf;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import com.yoshtec.owl.PropertyAccessType;
/**
 * This Class helps to access annotated Properties of 
 * Java Beans and link the concepts to the OWL world.
 * 
 * @author Jonas von Malottki
 *
 */
public interface PropertyAccessor{

	/**
	 * 
	 * @return {@code false} if this Property is an Array or a Collection   
	 */
	public boolean isSingleValue();

	/**
	 * Sets the value or in Case of Collections will add the Value to the
	 * Collection of the Object passed.
	 * 
	 * @param obj
	 * @param value
	 */
	public void setOrAddValue(Object obj, Object value);
	
	/**
	 * Commits the values from the cache to the Objects 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void commit() throws IllegalAccessException, InvocationTargetException; 

	/**
	 * Retrieves a Value from a Property. Primitives will be boxed.
	 * @param obj the Object the Value should be retrieved from
	 * @return the value of the Property from the Object
	 * @throws Exception
	 */
	public Object getValue(Object obj) throws IllegalAccessException, InvocationTargetException;
	
	public Collection<String> getLiterals(Object obj) throws Exception;
	
	/**
	 * @return the propUri
	 */
	public IRI getPropUri();
	/**
	 * @param propUri the propUri to set
	 */
	public void setPropUri(IRI propUri);
	
	/**
	 * @return the Set of Datatype URIs valid for this property
	 */
	public Set<IRI> getDataTypeUris();

	/**
	 * @return the declaringClass
	 */
	public Class<?> getDeclaringClass();

	public boolean isAccessible();
	
	/**
     * @return the functional
     */
    public boolean isFunctional();

    /**
     * @param functional the functional to set
     */
    public void setFunctional(boolean functional);

    /**
     * 
     * @return the Type of Property Access e.g. field or Setter/getter-Pair
     */
    public PropertyAccessType getAccess(); 

    Class<?> getType();
    
//    /**
//     * @return the Field if this Property is accessed via fields otherwise {@code null}
//     */
//    public Field getField() {
//        return this.field;
//    }
//
//    /**
//     * @return the Getter if this Property is accessed via setter/getter 
//     * pairs {@code null} otherwise. 
//     */
//    public Method getGetter() {
//        return this.getter;
//    }
//
//    /**
//     * @return the setter Method if this Property is accessed via setter/getter 
//     * pairs {@code null} otherwise. 
//     */
//    public Method getSetter() {
//        return this.setter;
//    }
}

