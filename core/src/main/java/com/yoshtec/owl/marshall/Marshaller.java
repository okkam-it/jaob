package com.yoshtec.owl.marshall;

import com.yoshtec.owl.XsdType;
import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.cf.ClassFacade;
import com.yoshtec.owl.cf.ClassFacadeFactory;
import com.yoshtec.owl.cf.PropertyAccessor;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.xml.bind.DatatypeConverter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.WriterDocumentTarget;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Marshalls annotated Java beans to Ontologies.
 * <p>
 * TODO: Maybe could also be used for a JPA like join/merge thing? but classes are then required to
 * have a OwlId annotation
 * </p>
 * 
 * @author Jonas von Malottki
 *
 */

public class Marshaller {

  private static final Logger LOG = LoggerFactory.getLogger(Marshaller.class);

  // whether the annotated axioms should be marshaled or not
  // private boolean createComplexAxioms = false;

  /** Local ontology to marshal to. */
  private OWLOntology ontology = null;

  /** Ontology Manager. */
  private OWLOntologyManager manager = null;

  /** Ontology Data factory. */
  private OWLDataFactory factory = null;

  /** Often used xsd:string. */
  private OWLDatatype owlString = null;

  /** Maps Java classes to xsd types and vice versa. */
  private XsdTypeMapper typeMapper = new XsdTypeMapper();

  /** List of already marshaled Objects. */
  private Map<Object, OWLIndividual> visitedObjects = null;

  /** Object where the OWL object properties are not yet marshaled. */
  private Stack<Object> missingObjectProps = null;

  /** already used and prepared classes. */
  private Map<Class<?>, ClassFacade> classes = new HashMap<Class<?>, ClassFacade>();

  private ClassFacadeFactory cfFactory;

  /**
   * Creates a new Marshaller to serialize annotated Java objects to an ontology.
   */
  public Marshaller() {

    // create a manger for Ontologies
    manager = OWLManager.createOWLOntologyManager();

    // We need a data factory to create various object from. Each ontology has a reference
    // to a data factory that we can use.
    factory = manager.getOWLDataFactory();

    // setting default string data type
    owlString = factory.getOWLDatatype(XsdType.STRING.getUri());

    resetObjects();
  }

  /**
   * Creates a new Ontology and the mapping for the URIs.
   */
  private OWLOntology createOntology(IRI ontologyUri, IRI physicalUri)
      throws OWLOntologyCreationException {

    // Set up a mapping, which maps the ontology IRI to the physical IRI
    addIriMapping(ontologyUri, physicalUri);

    // Now create the ontology - we use the ontology IRI (not the physical IRI)
    return manager.createOntology(ontologyUri);
  }

  /**
   * Rests local working Structures: meaning clears the visited Objects HashMap and the still not
   * marshaled object properties for the iterative marshaling.
   * 
   */
  private void resetObjects() {
    this.visitedObjects = new HashMap<Object, OWLIndividual>();
    this.missingObjectProps = new Stack<Object>();
  }

  /**
   * Marshal the ontology.
   * 
   * @param objects the objects to marshal
   * @param ontologyUri the ontology uri
   * @param deep recursively marshal
   * @return the marshalled ontology
   * @throws MarshalException if any exception occurs
   */
  public OWLOntology marshal(Collection<?> objects, IRI ontologyUri, boolean deep)
      throws MarshalException {
    if (ontologyUri == null) {
      throw new IllegalArgumentException("no ontology Uri set");
    }
    try {
      return this.marshal(objects, manager.createOntology(ontologyUri), deep);
    } catch (OWLOntologyCreationException e) {
      throw new MarshalException("Could not create the Ontology " + ontologyUri, e);
    }
  }

  /**
   * Creates an ontology, Marshals the Objects passed And saves the Ontology to the physical IRI
   * passed.
   * 
   * @param objects Objects to be saved to the ontology
   * @param ontologyUri The IRI of the Ontology
   * @param ontologyPhysicalUri the physical IRI to save the Ontology to, e.g. filename
   * @return the newly created Ontology
   * @throws MarshalException if marshalling had a Error
   */
  public OWLOntology marshal(Collection<?> objects, IRI ontologyUri, IRI ontologyPhysicalUri)
      throws MarshalException {
    return marshal(objects, ontologyUri, ontologyPhysicalUri, true);
  }

  /**
   * Creates an ontology, Marshalls the Objects passed And saves the Ontology to the physical IRI
   * passed.
   * 
   * @param objects Objects to be saved to the ontology
   * @param ontologyIri The IRI of the Ontology
   * @param ontologyPhysicalIri the physical IRI to save the Ontology to, e.g. filename
   * @param deep if the object graph should be traversed or not, if it is not traversed only the
   *        uris of the object Properties will be filled in.
   * @return the newly created Ontology
   * @throws MarshalException if the marshalling failed
   */
  public OWLOntology marshal(Collection<?> objects, IRI ontologyIri, IRI ontologyPhysicalIri,
      boolean deep) throws MarshalException {
    return marshal(objects, ontologyIri, ontologyPhysicalIri, null, deep);
  }

  public OWLOntology marshal(Collection<?> objects, IRI ontologyIri, Writer output, boolean deep)
      throws MarshalException {
    return marshal(objects, ontologyIri, null, output, deep);
  }

  /**
   * Creates an ontology (ABOX). Marshalls the Objects passed and saves the Ontology in the passed
   * Writer.
   * 
   * @param objects Objects to be saved to the ontology
   * @param ontologyIri The IRI of the Ontologyto
   * @param ontologyPhysicalIri the physical IRI of the ontology
   * @param writer the Writer to save the Ontology to, e.g. BufferedWriter
   * @param deep if the object graph should be traversed or not, if it is not traversed only the
   *        uris of the object Properties will be filled in.
   * @return the newly created Ontology
   * @throws MarshalException if the marshalling failed
   */
  public OWLOntology marshal(Collection<?> objects, IRI ontologyIri, IRI ontologyPhysicalIri,
      Writer writer, boolean deep) throws MarshalException {
    if (ontologyIri == null) {
      throw new IllegalArgumentException("No ontologyURI specified");
    }
    if (objects == null) {
      throw new IllegalArgumentException("No Objects to be marshalled");
    }

    // create the ontology
    OWLOntology ont;
    try {
      if (ontologyPhysicalIri == null) {
        ont = manager.createOntology(ontologyIri);
      } else {
        ont = createOntology(ontologyIri, ontologyPhysicalIri);
        this.marshal(objects, ont, deep);
      }
    } catch (OWLOntologyCreationException e) {
      throw new MarshalException("Error creating the ontology " + ontologyIri, e);
    }

    if (writer == null) {
      try {
        manager.saveOntology(ontology, ontologyPhysicalIri);
      } catch (UnknownOWLOntologyException e) {
        throw new MarshalException("Error saving the ontology to " + ontologyPhysicalIri, e);
      } catch (OWLOntologyStorageException e) {
        throw new MarshalException("Error saving the ontology to " + ontologyPhysicalIri, e);
      }
      return ontology;
    }

    OWLOntologyDocumentTarget target = new WriterDocumentTarget(writer);

    try {
      manager.saveOntology(ontology, target);
    } catch (UnknownOWLOntologyException e) {
      throw new MarshalException("Unable to write ontology to output", e);
    } catch (OWLOntologyStorageException e) {
      throw new MarshalException("Unable to write ontology to output", e);
    }

    return this.ontology;
  }

  /**
   * Convenience Method for {@code marshal(objects, onto, true)}.
   * 
   * @param objects the objects to be marshaled
   * @param onto the ontology to be marshaled to
   * @return the ontology passed
   * @throws MarshalException if the marshalling failed
   */
  public OWLOntology marshal(Collection<?> objects, OWLOntology onto) throws MarshalException {
    return marshal(objects, onto, true);
  }

  /**
   * Returns the passed ontology.
   * 
   * @param objects the objects to be marshaled
   * @param onto the ontology to be marshaled to
   * @param deep if the object graph should be traversed or not, if it is not traversed only the
   *        uris of the object Properties will be filled in.
   * @return the ontology passed
   * @throws MarshalException if the marshalling failed
   */
  public OWLOntology marshal(Collection<?> objects, OWLOntology onto, boolean deep)
      throws MarshalException {
    if (objects == null) {
      throw new IllegalArgumentException("No Objects to be marshaled");
    }
    if (onto == null) {
      throw new IllegalArgumentException("Ontology shall not be null");
    }

    this.ontology = onto;

    resetObjects();

    lmarshal(objects, deep);

    return ontology;
  }

  /**
   * Return a class facade able to handle the Object {@code o}.
   * 
   * @param o the object
   * @return A class facade able to handle the Object {@code o}
   * @throws Exception if any exception occurs
   */
  private ClassFacade getClassFacade(Object o) throws OWLOntologyChangeException {
    ClassFacade cf = classes.get(o.getClass());
    if (cf == null) {
      cf = getCfFactory().createClassFacade(o.getClass());
      classes.put(o.getClass(), cf);

      // Base Uri of the Package
      // TODO -------------------------------------
      IRI ontoBaseIri = cf.getOntoBaseUri();
      OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(ontoBaseIri);
      manager.applyChange(new AddImport(ontology, importDeclaraton));
      // manager.addAxiom(ontology, owlImportsDeclaration);
      try {
        manager.loadOntology(importDeclaraton.getIRI());
      } catch (OWLOntologyCreationException ex) {
        LOG.error("Error while loading imported ontology '" + ontoBaseIri + "'", ex);
      }
    }
    return cf;
  }

  /**
   * Returns the {@link OWLIndividual} for the parameter {@code o}.
   * 
   * @return the {@link OWLIndividual} for the parameter {@code o}
   */
  private OWLIndividual getOwlIndividual(Object o, boolean deep)
      throws OWLOntologyChangeException, MarshalException {

    if (!visitedObjects.containsKey(o)) {

      ClassFacade cf = getClassFacade(o);

      // Individual creation
      final IRI ontologyIri = ontology.getOntologyID().getOntologyIRI();
      final IRI individualUri = IRI.create(ontologyIri.toString(), cf.getIdString(o));
      final OWLNamedIndividual ind = factory.getOWLNamedIndividual(individualUri);

      // add the visited Object
      visitedObjects.put(o, ind);

      // add the Class URIs
      for (IRI uri : cf.getClassUris()) {
        if (uri != null) {
          OWLClass ocls = factory.getOWLClass(uri);
          manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(ocls, ind));
        }
      }

      // Data Properties
      for (PropertyAccessor prop : cf.getDataProperties()) {
        try {
          addDataProperty(prop.getValue(o), ind, prop);
        } catch (IllegalAccessException e) {
          throw new MarshalException("Error in accessing object values from object " + o
              + " data property: " + prop.getPropUri(), e);
        } catch (InvocationTargetException e) {
          throw new MarshalException("Error in accessing object values from object " + o
              + " data property: " + prop.getPropUri(), e);
        }
      }

      // only if deep is set we will descend further in the object graph
      if (deep) {
        // Object Property still missing
        this.missingObjectProps.push(o);
      }
    }
    return visitedObjects.get(o);
  }

  /**
   * Marshals all Objects from the missingObjectProps.
   * 
   * @throws MarshalException if something goes wrong
   */
  private void lmarshal(Collection<?> objects, boolean deep) throws MarshalException {

    if (objects == null) {
      return; // nothing to to!
    }

    try {
      // two Phase system

      // first marshal the plain currently known objects
      for (Object obj : objects) {
        if (obj != null) { // sort nasty nulls out
          // this will add for every unknown object a missing object prop
          getOwlIndividual(obj, true);
        }
      }

      // then fill in the missing object properties
      // while also following the object graph and
      // discover new missing Individuals with its Object props
      while (!missingObjectProps.isEmpty()) {

        final Object obj = missingObjectProps.pop();
        final ClassFacade cf = getClassFacade(obj);
        final OWLIndividual ind = getOwlIndividual(obj, deep);

        // Object Properties
        for (PropertyAccessor prop : cf.getObjectProperties()) {
          try {
            addObjectProperty(prop.getValue(obj), ind, prop, deep);
          } catch (InvocationTargetException e) {
            throw new MarshalException("Error in accessing object values from object " + obj
                + " property: " + prop.getPropUri(), e);
          } catch (IllegalAccessException e) {
            throw new MarshalException("Error in accessing object values from object " + obj
                + " property: " + prop.getPropUri(), e);
          }
        }
      }
    } catch (OWLOntologyChangeException e) {
      throw new MarshalException(e); // TODO: message
    } catch (IllegalStateException e) {
      throw new MarshalException(e);
    } catch (IllegalArgumentException e) {
      throw new MarshalException(e);
    }
  }

  private void addDataProperty(Object value, OWLIndividual ind, PropertyAccessor prop)
      throws OWLOntologyChangeException {
    if (value != null) {

      // Datatype of the Property: defaulting to String
      OWLDatatype dt = owlString;

      // if it is mapped to a data Property
      Set<IRI> dturis = prop.getDataTypeUris();
      if (dturis != null && !dturis.isEmpty()) {
        if (dturis.size() == 1) {
          // get the first uri
          dt = factory.getOWLDatatype(dturis.iterator().next());
        } else {
          LOG.warn("Cannot handle heterogenous DataProperties: {}", dturis);
        }
      } else {
        LOG.warn("No DataType set, defaulting to xsd:string: {}, {}", prop, ind);
      }

      // Process the values:
      IRI propuri = prop.getPropUri();
      // unpack Lists
      if (value instanceof Collection<?>) {
        for (Object lv : ((Collection<?>) value)) {
          String printValue = printValue(lv);
          if (printValue != null) {
            addDataPropertyValue(ind, propuri, dt, printValue);
          }
        }
      } else if (value instanceof Object[]) { // or Object Arrays
        for (Object lv : ((Object[]) value)) {
          addDataPropertyValue(ind, propuri, dt, printValue(lv));
        }
      } else { // seems to be a single Value
        addDataPropertyValue(ind, propuri, dt, printValue(value));
      }
    }
  }


  private void addDataPropertyValue(OWLIndividual ind, IRI property, OWLDatatype dt, String literal)
      throws OWLOntologyChangeException {
    OWLDataProperty odp = factory.getOWLDataProperty(property);
    OWLLiteral odc = factory.getOWLLiteral(literal, dt);
    manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(odp, ind, odc));
  }

  /**
   * Processes Object Properties.
   * 
   * @param value the Value
   * @param ind the OWLIndividual to which the Property shall be added
   * @param prop the property to set
   * @throws Exception if any exception occurs
   */
  private void addObjectProperty(Object value, OWLIndividual ind, PropertyAccessor prop,
      boolean deep) throws OWLOntologyChangeException, MarshalException {

    if (value != null) {
      // Process the values:
      OWLObjectProperty oprop = factory.getOWLObjectProperty(prop.getPropUri());

      // unpack Lists
      if (value instanceof Collection<?>) {
        for (Object lv : ((Collection<?>) value)) {
          OWLIndividual oobj = this.getOwlIndividual(lv, deep);
          addObjectPropertyValue(ind, oprop, oobj);
        }
      } else if (value instanceof Object[]) { // or Object Arrays
        for (Object lv : ((Object[]) value)) {
          OWLIndividual oobj = this.getOwlIndividual(lv, deep);
          addObjectPropertyValue(ind, oprop, oobj);
        }
      } else { // seems to be a single Value
        OWLIndividual oobj = this.getOwlIndividual(value, deep);
        addObjectPropertyValue(ind, oprop, oobj);
      }
    }
  }

  private void addObjectPropertyValue(OWLIndividual subj, OWLObjectProperty pred,
      OWLIndividual obj) {
    OWLObjectPropertyAssertionAxiom addOVal =
        factory.getOWLObjectPropertyAssertionAxiom(pred, subj, obj);
    try {
      manager.addAxiom(ontology, addOVal);
    } catch (OWLOntologyChangeException e) {
      LOG.warn("Unable to create {} <{}> {}", new Object[] {subj, pred, obj});
    }
  }

  /**
   * Prints the value of an Object.
   * 
   * @param value the value
   * @return the string representation of the passed object
   */
  protected String printValue(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Calendar) {
      return DatatypeConverter.printDateTime((Calendar) value);
    }
    if (value instanceof java.util.Date) {
      Calendar cal = new GregorianCalendar();
      cal.setTime((java.util.Date) value);
      return DatatypeConverter.printDateTime(cal);
    }
    return value.toString();
  }

  /**
   * IRI Mappings can be added to this Marshaller via the Ontology Manager.
   *
   * @return the manager of this Marshaller.
   */
  public OWLOntologyManager getManager() {
    return manager;
  }

  /**
   * Returns the current XSD type mapper.
   * 
   * @return the current type mapping
   */
  public XsdTypeMapper getTypeMapper() {
    return this.typeMapper;
  }

  /**
   * Sets the type mapper. This is only neccessary if you want to override the default type mapping
   * between Java types and the owl/xsd types
   * 
   * @param typeMapper new type mapping
   * @see XsdTypeMapper
   */
  public void setTypeMapper(XsdTypeMapper typeMapper) {
    if (this.typeMapper == typeMapper) {
      return;
    }
    this.typeMapper = typeMapper;
    cfFactory = new ClassFacadeFactory(typeMapper);
  }

  private ClassFacadeFactory getCfFactory() {
    if (cfFactory == null) {
      cfFactory = new ClassFacadeFactory(typeMapper);
    }
    return cfFactory;
  }

  /**
   * Adds the passed IRI mapper to the manager.
   * 
   * @param ontoIri the ontology IRI to map
   * @param physicalOntoIri the physical location of the IRI to map
   */
  public void addIriMapping(IRI ontoIri, IRI physicalOntoIri) {
    if (physicalOntoIri != null) {
      manager.addIRIMapper(new SimpleIRIMapper(ontoIri, physicalOntoIri));
    }
  }

}
