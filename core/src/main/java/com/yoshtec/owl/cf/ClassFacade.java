package com.yoshtec.owl.cf;

import com.yoshtec.owl.XsdTypeMapper;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;

/**
 * Is a proxy for Accessing the Class and reading the Properties of the Objects.
 * 
 * @author Jonas von Malottki
 *
 */
public interface ClassFacade {

  Class<?> getRepresentedClass();

  PropertyAccessor getProperty(IRI uri);

  boolean hasProperty(IRI uri);

  /** Returns the ontoBaseUri retrieved from the Package. */
  IRI getOntoBaseUri();

  String getIdString(Object o);

  Set<IRI> getImportedUris();


  /** Returns the Set of OwlClass URIs handled by this SimpleClassFacade. */
  Set<IRI> getClassUris();

  Collection<PropertyAccessor> getDataProperties();

  Collection<PropertyAccessor> getObjectProperties();

  boolean handlesClass(IRI uri);

  Object getNewInstance() throws Exception;

  Object getNewInstance(String id) throws IllegalArgumentException, InstantiationException,
      IllegalAccessException, InvocationTargetException;

  boolean hasSetableId();

  void setId(Object obj, String id);

  /**
   * Commits the cached values to the Objects.
   * 
   * @throws InvocationTargetException if the setter/getter could not be invoked correctly.
   * @throws IllegalAccessException if the Access to the Properties has been denied or failed.
   */
  void commit() throws InvocationTargetException, IllegalAccessException;

  XsdTypeMapper getTypeMapper();

  void setTypeMapper(XsdTypeMapper typeMapper);

}
