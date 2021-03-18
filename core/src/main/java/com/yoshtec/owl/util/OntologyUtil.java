package com.yoshtec.owl.util;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some helping methods.
 * 
 * @author Jonas von Malottki
 *
 */
public class OntologyUtil {

  private final static Logger log = LoggerFactory.getLogger(OntologyUtil.class);

  public final static URI OWL_THING_URI = URI.create("http://www.w3.org/2002/07/owl#Thing");

  /**
   * gets all classes somehow described in this OWLDescription that is:
   * <ul>
   * <li>Single OWLClasses</li>
   * <li>Through ObjectUnionOf Nested OWLClasses</li>
   * </ul>
   * 
   * Currently lacks to handle OWLObjectIntersectionOf
   * 
   * @param odes
   * @return Owlclasses
   */
  public static Set<OWLClass> getOWLClasses(OWLClassExpression odes) {
    Set<OWLClass> result = new HashSet<OWLClass>();

    // if(odes.isAnonymous()){
    // LogUtil.logObjectInfo(odes, log);
    // }
    Stack<OWLClassExpression> desstack = new Stack<OWLClassExpression>();

    desstack.push(odes);

    while (!desstack.isEmpty()) {
      OWLClassExpression des = desstack.pop();

      if (!des.isAnonymous()) {
        try {
          // make it a class
          OWLClass ocls = des.asOWLClass();

          result.add(ocls);

        } catch (Exception e) {
          log.error("Error casting to OWLClass", e);
        }
      } else {
        if (des instanceof OWLNaryBooleanClassExpression) {
          if (des instanceof OWLObjectUnionOf) {
            OWLObjectUnionOf ounion = (OWLObjectUnionOf) des;
            for (OWLClassExpression ldes : ounion.getOperands()) {
              desstack.push(ldes);
            }
          } else {
            log.warn("Unable to Handle this Class construct: {}", des);
          }
        } else {
          log.warn("Unable to handle this Class construct: {}", des);
        }
      }
    }
    return result;
  }

  /**
   * Convenience method for retrieving a set of classes from a Collection of OWLDescription.
   * 
   * @param odesc
   * @return Set of OWL classes form the OWLDescriptions
   */
  public static Set<OWLClass> getOWLClasses(Collection<OWLClassExpression> odesc) {
    Set<OWLClass> result = new HashSet<OWLClass>();
    for (OWLClassExpression odes : odesc) {
      result.addAll(getOWLClasses(odes));
    }
    return result;

  }

  /**
   * Return the "sanitized" name of the passed ontology class.
   * 
   * @param ocls the ontology class
   * @return the "sanitized" name of the passed ontology class
   */
  public static String getClassName(OWLClass ocls) {
    if (ocls.getIRI().getFragment() == null) {
      throw new IllegalArgumentException(ocls.getIRI() + " is not a legal class name");
    }
    return ocls.getIRI().getFragment().toString();
  }
}
