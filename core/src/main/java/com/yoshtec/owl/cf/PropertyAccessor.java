package com.yoshtec.owl.cf;

import com.yoshtec.owl.PropertyAccessType;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;

/**
 * This Class helps to access annotated Properties of Java Beans and link the concepts to the OWL
 * world.
 * 
 * @author Jonas von Malottki
 *
 */
public interface PropertyAccessor {

  /**
   * Returns {@code false} if this Property is an Array or a Collection.
   * 
   * @return {@code false} if this Property is an Array or a Collection
   */
  public boolean isSingleValue();

  /**
   * Sets the value or in Case of Collections will add the Value to the Collection of the Object
   * passed.
   * 
   * @param obj the object
   * @param value the value
   */
  public void setOrAddValue(Object obj, Object value);

  /**
   * Commits the values from the cache to the Objects.
   * 
   * @throws IllegalAccessException if any error occurs
   * @throws InvocationTargetException if any error occurs
   */
  public void commit() throws IllegalAccessException, InvocationTargetException;

  /**
   * Retrieves a Value from a Property. Primitives will be boxed.
   * 
   * @param obj the Object the Value should be retrieved from
   * @return the value of the Property from the Object
   * @throws Exception if any error occurs
   */
  public Object getValue(Object obj) throws IllegalAccessException, InvocationTargetException;

  public Collection<String> getLiterals(Object obj) throws Exception;

  /**
   * Returns the propUri.
   * 
   * @return the propUri
   */
  public IRI getPropUri();

  /**
   * Sets the propUri.
   * 
   * @param propUri the propUri to set
   */
  public void setPropUri(IRI propUri);

  /**
   * Returns the Set of Datatype URIs valid for this property.
   * 
   * @return the Set of Datatype URIs valid for this property
   */
  public Set<IRI> getDataTypeUris();

  /**
   * Returns the declaring class.
   * 
   * @return the declaringClass
   */
  public Class<?> getDeclaringClass();

  public boolean isAccessible();

  /**
   * Returns the functional.
   * 
   * @return the functional
   */
  public boolean isFunctional();

  /**
   * Sets the functional.
   * 
   * @param functional the functional to set
   */
  public void setFunctional(boolean functional);

  /**
   * Returns the Type of Property Access e.g. field or Setter/getter-Pair.
   * 
   * @return the Type of Property Access e.g. field or Setter/getter-Pair
   */
  public PropertyAccessType getAccess();

  Class<?> getType();

  // /**
  // * @return the Field if this Property is accessed via fields otherwise {@code null}
  // */
  // public Field getField() {
  // return this.field;
  // }
  //
  // /**
  // * @return the Getter if this Property is accessed via setter/getter
  // * pairs {@code null} otherwise.
  // */
  // public Method getGetter() {
  // return this.getter;
  // }
  //
  // /**
  // * @return the setter Method if this Property is accessed via setter/getter
  // * pairs {@code null} otherwise.
  // */
  // public Method getSetter() {
  // return this.setter;
  // }
}

