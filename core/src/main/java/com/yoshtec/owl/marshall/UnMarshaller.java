package com.yoshtec.owl.marshall;

import com.yoshtec.owl.Const;
import com.yoshtec.owl.XsdType;
import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.OwlOntology;
import com.yoshtec.owl.annotations.OwlRegistry;
import com.yoshtec.owl.cf.ClassFacade;
import com.yoshtec.owl.cf.ClassFacadeFactory;
import com.yoshtec.owl.cf.PropertyAccessor;
import com.yoshtec.owl.util.ClassUtil;
import com.yoshtec.owl.util.OntologyUtil;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unmarshals OWL Ontologies into Java Objects.
 * 
 * @author Jonas von Malottki
 *
 */
public final class UnMarshaller {

  private static final Logger LOG = LoggerFactory.getLogger(UnMarshaller.class);

  /** The owl ontology Manager. */
  protected final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

  // only classes should be in there, no interfaces or other
  protected Map<IRI, ClassFacade> registeredClasses = new HashMap<IRI, ClassFacade>();

  /** List of already unmarshalled Objects. */
  protected Map<IRI, Object> unmarshalledObjects = null;

  /** List of the object properties that are not yet connected. */
  protected List<ObjectPropHolder> unresolvedObjectProperties = null;

  /** the Ontology. */
  protected OWLOntology ontology = null;

  /** Default type mapper. */
  protected XsdTypeMapper typeMapper;

  private ClassFacadeFactory cfFactory;


  /**
   * Holds the information necessary to associate object properties and their values with the
   * unmarshalled objects.
   */
  private static class ObjectPropHolder {
    public IRI owlInduri = null;
    public ClassFacade cf = null;
    public IRI propUri = null;
    public Object instance = null;

    public ObjectPropHolder(IRI owlInduri, ClassFacade cf, IRI propUri, Object instance) {
      super();
      this.owlInduri = owlInduri;
      this.cf = cf;
      this.propUri = propUri;
      this.instance = instance;
    }
  }

  public UnMarshaller() {
    this(new XsdTypeMapper());
  }

  /**
   * Constructs a new UnMarshaller with the passed type mapper.
   * 
   * @param typeMapper the XSD type mapper
   */
  public UnMarshaller(XsdTypeMapper typeMapper) {
    this.typeMapper = typeMapper == null ? new XsdTypeMapper() : typeMapper;
    this.cfFactory = new ClassFacadeFactory(this.typeMapper);
  }


  /**
   * Adds a mapping between an ontology URI and its physical location.
   * 
   * @param ontologyUri the ontology URI
   * @param physicalUri the onology physical URI
   */
  public void addUriMapping(IRI ontologyUri, IRI physicalUri) {
    SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyUri, physicalUri);
    manager.addIRIMapper(mapper);
  }

  /**
   * Adds an URI mapper.
   * 
   * @param mapper the ontology URI mappper
   */
  public void addUriMapper(OWLOntologyIRIMapper mapper) {
    manager.addIRIMapper(mapper);
  }


  /**
   * Unmarshall the passed object.
   * 
   * @param ontologyPhysicalUri the ontology physical URI
   * @param individualUri the individual URI
   * @return the individual specified by the individualUri if existent else null;
   * @throws UnmarshalException if unmarshalling fails
   */
  public <T> T unmarshall(IRI ontologyPhysicalUri, IRI individualUri) throws UnmarshalException {
    throw new UnsupportedOperationException();// TODO
    // return null;
  }

  /**
   * Unmarshal the passed individual.
   * 
   * @param oi the named individual
   * @throws IllegalArgumentException if any exception occurs
   * @throws InstantiationException if any exception occurs
   * @throws IllegalAccessException if any exception occurs
   * @throws InvocationTargetException if any exception occurs
   */
  protected void unmarshall(OWLNamedIndividual oi) throws IllegalArgumentException,
      InstantiationException, IllegalAccessException, InvocationTargetException {

    IRI individualIri = oi.getIRI();
    if (!unmarshalledObjects.containsKey(individualIri)) {
      // check which Object shall be instantiated
      Set<OWLClass> oclasses = OntologyUtil.getOwlClasses(oi.getTypes(ontology));
      LOG.debug("OWLClasses from Individual {}: {}", oi, oclasses);

      // check if more than one JavaClass is asserted to be
      // the class of the individual
      if (oclasses.size() > 1) {
        LOG.warn("More than one Class is asserted for ind: {} :{}", oi.toString(), oclasses);
      }

      // take the last one
      // TODO: could also be an instance of ??
      ClassFacade cf = null;
      for (OWLClass ocls : oclasses) {
        IRI iri = ocls.getIRI();
        LOG.debug(iri.toString());
        if (registeredClasses.containsKey(iri)) {
          cf = registeredClasses.get(iri);
        } else {
          iri = IRI.create("#" + iri.getFragment());
          if (registeredClasses.containsKey(iri)) {
            cf = registeredClasses.get(iri);
          }
        }
      }

      if (cf == null) {
        LOG.warn("No SimpleClassFacade found for Individual {}", oi);
        // put the IRI to the list, at least we tried for this individual
        unmarshalledObjects.put(individualIri, null);
        // prematurely end
        return;
      }

      // Instantiation
      String id = oi.toString();
      Object myObj = cf.getNewInstance(id);
      unmarshalledObjects.put(individualIri, myObj);
      if (cf.hasSetableId()) {
        cf.setId(myObj, id);
      }

      // Data Properties!
      this.addDataProperties(oi, cf, myObj);

      // Object Properties
      this.addObjectProperties(oi, cf, myObj);
      // System.out.println(myObj);
    }
  }

  /**
   * Unmarshals the ontology represented from the physicalUri <br>
   * Imports in the Ontology should be made Accessible via a Mapping
   * {@link #addUriMapping(IRI, IRI)} or {@link #addUriMapper(OWLOntologyURIMapper)}.
   * 
   * @param physicalUri the physical IRI of the ontology
   * @return the collection of objects unmarshalled
   * @throws UnmarshalException if Unmarshaling fails
   * @throws FileNotFoundException if the physicalUri refers to a file that does not exists
   */
  public Collection<Object> unmarshal(IRI physicalUri)
      throws UnmarshalException, FileNotFoundException {

    OWLOntology onto;
    try {
      onto = manager.loadOntologyFromOntologyDocument(physicalUri);
    } catch (OWLOntologyCreationException e) {
      if (e.getCause() instanceof FileNotFoundException) {
        throw (FileNotFoundException) e.getCause();
      }
      throw new UnmarshalException("Error opening Ontology " + physicalUri, e);
    }
    return this.unmarshal(onto);
  }

  /**
   * Unmarshal the passed input stream.
   * 
   * @param is the InputStream
   * @return the unmarshalled objects
   * @throws UnmarshalException if any exception occurs
   */
  public Collection<Object> unmarshal(InputStream is) throws UnmarshalException {
    OWLOntology onto;
    try {
      onto = manager.loadOntologyFromOntologyDocument(is);
    } catch (OWLOntologyCreationException e) {
      throw new UnmarshalException("Error opening Ontology from InputStream", e);
    }
    return this.unmarshal(onto);
  }

  /**
   * Unmarshals all objects from an ontology.
   * 
   * @param onto the OWLOntology
   * @return the collection of objects unmarshalled
   * @throws UnmarshalException if umarshalling failed
   */
  public Collection<Object> unmarshal(OWLOntology onto) throws UnmarshalException {

    this.ontology = onto;

    // initialize some variables
    this.unmarshalledObjects = new HashMap<IRI, Object>();
    this.unresolvedObjectProperties = new ArrayList<ObjectPropHolder>();

    // first unmarshall normal individuals
    Set<OWLNamedIndividual> individualsInSignature = ontology.getIndividualsInSignature();
    for (OWLNamedIndividual oi : individualsInSignature) {
      try {
        unmarshall(oi);
      } catch (IllegalArgumentException e) {
        throw new UnmarshalException("Error unmarshalling the individual " + oi, e);
      } catch (InstantiationException e) {
        throw new UnmarshalException("Error unmarshalling the individual " + oi, e);
      } catch (IllegalAccessException e) {
        throw new UnmarshalException("Error unmarshalling the individual " + oi, e);
      } catch (InvocationTargetException e) {
        throw new UnmarshalException("Error unmarshalling the individual " + oi, e);
      }
    }

    // then build the object graph
    for (ObjectPropHolder prop : unresolvedObjectProperties) {
      Object value = unmarshalledObjects.get(prop.owlInduri);
      // System.out.println("Setting prop "+ prop.propUri+ " for "+ value);
      PropertyAccessor property = prop.cf.getProperty(prop.propUri);
      property.setOrAddValue(prop.instance, value);
    }

    Collection<ClassFacade> rcv = registeredClasses.values();
    for (ClassFacade cf : rcv) {
      try {
        cf.commit();
      } catch (InvocationTargetException e) {
        throw new UnmarshalException("Error setting the value to object " + cf, e);
      } catch (IllegalAccessException e) {
        throw new UnmarshalException("Error setting the value to object " + cf, e);
      }
    }

    // copy values and sort out null objects
    Set<Object> result = new HashSet<Object>();
    for (Object obj : unmarshalledObjects.values()) {
      if (obj != null) {
        result.add(obj);
      }
    }

    // cleanup
    this.unresolvedObjectProperties = null;
    this.unmarshalledObjects = null;

    return result;
  }

  private void addObjectProperties(OWLIndividual oi, ClassFacade cf, Object instance) {
    for (Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> opentry : oi
        .getObjectPropertyValues(ontology).entrySet()) {

      // retrieve current property IRI
      IRI propUri = opentry.getKey().asOWLObjectProperty().getIRI();

      if (cf.hasProperty(propUri)) {

        // Walk through the values
        for (OWLIndividual ocd : opentry.getValue()) {
          // get the value an Set the Property Value
          if (ocd instanceof OWLNamedIndividual) {
            OWLNamedIndividual oncd = (OWLNamedIndividual) ocd;
            ObjectPropHolder ph = new ObjectPropHolder(oncd.getIRI(), cf, propUri, instance);
            unresolvedObjectProperties.add(ph);
          }
        }
      }
    }
  }


  private void addDataProperties(OWLIndividual oi, ClassFacade cf, Object instance) {
    Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropertyValues =
        oi.getDataPropertyValues(ontology);
    for (Entry<OWLDataPropertyExpression, Set<OWLLiteral>> dpEntry : dataPropertyValues
        .entrySet()) {

      // retrieve current property IRI
      IRI propUri = dpEntry.getKey().asOWLDataProperty().getIRI();

      if (cf.hasProperty(propUri)) {

        // Walk through the values
        for (OWLLiteral ocd : dpEntry.getValue()) {
          // try to read the literal
          Object value = readValue(ocd.getLiteral(), ocd.getDatatype().getIRI());

          // Set the Property Value
          PropertyAccessor property = cf.getProperty(propUri);
          property.setOrAddValue(instance, value);
        }
      }
    }
  }

  // TODO: Improve reading and printing values in a more JAXB manner
  private Object readValue(String literal, IRI dt) {
    XsdType xsdType = XsdType.fromIri(dt);
    Class<?> type = typeMapper.getType(xsdType);

    if (type.isAssignableFrom(Calendar.class)) {
      return DatatypeConverter.parseDateTime(literal);
    }

    if (type.isAssignableFrom(String.class)) {
      return literal;
    }

    if (type.isAssignableFrom(Integer.class)) {
      return Integer.parseInt(literal);
    }

    if (type.isAssignableFrom(Float.class)) {
      return Float.parseFloat(literal);
    }

    if (type.isAssignableFrom(Boolean.class)) {
      return Boolean.parseBoolean(literal);
    }

    if (type.isAssignableFrom(Double.class)) {
      return Double.parseDouble(literal);
    }

    if (type.isAssignableFrom(Long.class)) {
      return Long.parseLong(literal);
    }

    return null;
  }


  /**
   * Convenience method for {@link #registerClass(Class)}.
   */
  public int registerClass(String className) throws ClassNotFoundException {
    Class<?> clazz = Class.forName(className);
    return registerClass(clazz);
  }


  /**
   * Registers a Class with the Unmarshaller. Only objects from registered classes in will be
   * unmarshalled.
   * <p>
   * This behavior is intentional, since through imports in an ontology it is very likely to have
   * Objects that are out of the scope and should not be unmarshalled.
   * </p>
   * <p>
   * Only classes that are annotated with {@code @OwlClass} or implementing a Interface that has be
   * annotated with {@code @OwlClass} will be registered.
   * </p>
   * TODO: handle Enums
   * 
   * @param clazz Class to be registered
   * @return if the Class successfully registered
   * @see OwlClass
   */
  public int registerClass(Class<?> clazz) {

    // do not add Interfaces, annotations for now
    if (clazz.isInterface() || clazz.isAnnotation()) {
      return 0;
    }

    int result = 0;

    if (clazz.isAnnotationPresent(OwlRegistry.class)) {
      // this class is a owl Registry
      for (Method method : clazz.getMethods()) {
        if (method.getName().startsWith(Const.CREATE_PREFIX)) {
          try {
            Object cn = method.invoke(clazz.newInstance());
            result += this.registerClass(cn.getClass());
          } catch (Exception e) {
            LOG.warn(
                "could not create instance or invoke create method " + method.toGenericString(), e);
          }
        }
      }
      return result;
    }



    ClassFacade cf = cfFactory.createClassFacade(clazz);

    // First option:
    // true generic owlclass
    {
      OwlClass oc = clazz.getAnnotation(OwlClass.class);
      if (oc != null) {
        // this class is annotated
        LOG.debug("registered OwlClass uri='{}' with Class {}", oc.uri(), clazz);

        for (IRI curi : cf.getClassUris()) {
          LOG.debug("registered OwlClass uri='{}' with Class {}", curi, clazz);
          registeredClasses.put(curi, cf);
        }

        return 1;
      }
    }

    // Second Option: specifically annotated to be an implementation
    // False owl Class, just implementing interfaces that represent the owlclass
    // TODO: this should be done in order to use Interfaces
    {
      OwlClassImplementation oci = clazz.getAnnotation(OwlClassImplementation.class);
      if (oci != null) {
        for (Class<?> iface : oci.value()) {
          if (iface.isInterface()) {
            OwlClass loc = iface.getAnnotation(OwlClass.class);
            if (loc != null) {
              registeredClasses.put(IRI.create(loc.uri()), cf);
              LOG.debug("registered OwlClass uri='{}' with Class {}", loc.uri(), clazz);
              result++;
            } else {
              LOG.warn("{} associated Interface ({}) is not annotated with @OwlClass", clazz,
                  iface);
            }
          } else {
            LOG.warn("Implementation of an OwlClass '{}' not associated with an Interface '{}' ",
                clazz, iface);
          }
        }
      }
    }

    // Third option: Looking for implemented interfaces and their annotations.
    for (Class<?> iface : clazz.getInterfaces()) {
      // doing this only for the first level, eg. direct implemented interfaces
      OwlClass oa = iface.getAnnotation(OwlClass.class);
      if (oa != null) {
        // this class is annotated
        registeredClasses.put(IRI.create(oa.uri()), cf);
        LOG.debug("registered OwlClass uri='{}' with Class {}", oa.uri(), clazz);
        result++;
      }
    }
    return result;
  }

  /**
   * Only Packages with the {@code @OwlOntology} Annotation will be registered. Shortcut for
   * registering the Java classes of an ontology manually.
   * 
   * @param packagename Java name of the package to be registered
   * @return if the package successfully registered
   * @see OwlOntology
   */
  public int registerPackage(String packagename) {
    int result = 0;
    try {
      Package pack = Package.getPackage(packagename);

      OwlOntology oannot = pack.getAnnotation(OwlOntology.class);
      if (oannot != null) {
        for (Class<?> clazz : ClassUtil.getClassesForPackage(packagename)) {
          result += registerClass(clazz);
        }

      }
      return result;
    } catch (Exception e) {
      LOG.warn("failed to register Package " + packagename, e);
      // TODO better error handling here
    }
    return result;
  }

  /**
   * Returns the current type mapping.
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

}
