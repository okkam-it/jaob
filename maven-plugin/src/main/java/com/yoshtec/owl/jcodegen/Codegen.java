package com.yoshtec.owl.jcodegen;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JJavaName;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.yoshtec.owl.PropertyType;
import com.yoshtec.owl.XsdType;
import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.annotations.OwlOntology;
import com.yoshtec.owl.annotations.OwlRegistry;
import com.yoshtec.owl.annotations.ontology.OwlImports;
import com.yoshtec.owl.util.LogUtil;
import com.yoshtec.owl.util.OntologyUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Class generates iCode out of an ontology, where most of the declared classes will result in
 * a Java class and property declarations will be added to them with the appropriate setter and
 * getter methods.
 * <p>
 * <h2>Example:</h2>
 * 
 * <blockquote>
 * 
 * <pre>
 * Codegen codegen = new Codegen();
 * codegen.setJavaPackageName(&quot;mypackage&quot;);
 * codegen.setOntologyIri(&quot;some.ontology.uri&quot;);
 * codegen.setOntologyPhysicalIri(new File(&quot;filename.owl&quot;).toURI().toString());
 * codegen.setJavaSourceFolder(new File(&quot;/My/Source/Folder&quot;));
 * codegen.genCode();
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author Jonas von Malottki
 * @author Flavio Pompermaier
 * 
 */

public class Codegen {

  private static final Logger LOG = LoggerFactory.getLogger(Codegen.class);

  // Settings

  /** logical IRI of the Ontology. */
  private String ontologyIriStr = null;

  /** physical IRI of the Ontology. */
  private String ontologyPhysicalIriStr = null;

  /** Java Package name to be created. */
  private String javaPackageName = null;

  /** source folder to put generated classes in. */
  private File javaSourceFolder = null;

  /** Suffix for an of an Interface. */
  private String javaClassSuffix = "Impl";

  /** Class Name for the owl:thing Class. */
  private String owlthingclassname = "Thing";

  /** whether a Object Factory Class should be generated. */
  private boolean createObjectFactory = true;

  /** name of the Object Factory Class. */
  private String objectFactoryName = "ObjectFactory";

  /** if a OWL ID Property shall be generated. */
  private boolean generateIdField = false;

  /** name of the OWL ID field / property. */
  private String idFieldName = "name";

  /** if interfaces should be generated. */
  private boolean generateInterfaces = true;

  // local work variables
  private OWLOntology ontology = null;
  private JCodeModel jmodel = null;
  private JPackage jpack = null;

  private final XsdTypeMapper typeMapper = new XsdTypeMapper();

  /** holds the interfaces and the Properties. */
  private Map<String, JInterfaceProxy> interfaces = new HashMap<String, JInterfaceProxy>();


  private List<String> ignoreProperties = new ArrayList<>();

  public static List<String> ignoredAbstractClassIris = new ArrayList<>();

  /**
   * Creates a new Code Generator.
   */
  public Codegen() {}

  /**
   * Loads an Ontology.
   * 
   * @return the loaded ontology
   * @throws OWLOntologyCreationException if any exception occurs
   * @throws Exception if any exception occurs
   */
  private OWLOntology loadOntology() throws OWLOntologyCreationException {

    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    OWLOntology result = null;

    IRI ontologyIri = IRI.create(ontologyIriStr);

    if (ontologyPhysicalIriStr != null && ontologyIriStr != null) {
      // Create a physical IRI which can be resolved to point to where our
      // ontology will be saved.
      IRI physicalIri = IRI.create(ontologyPhysicalIriStr);

      // Set up a mapping, which maps the ontology IRI to the physical IRI
      SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIri, physicalIri);
      manager.addIRIMapper(mapper);
      // manager.addIRIMapper(new NonMappingOntologyIRIMapper());

      LOG.debug("Attempting to load Ontology (IRI={}) from physical IRI={}", ontologyIri,
          physicalIri);

      result = manager.loadOntology(physicalIri);

    } else {
      LOG.debug("Attempting to load Ontology from IRI={}", ontologyIri);
      result = manager.loadOntology(ontologyIri);
    }

    LOG.info("Successfully loaded Ontology IRI={}", ontologyIri);

    return result;

  }

  /**
   * Generates java code for a given Ontology.
   * <p>
   * The Java code makes heavily use of Annotations. And will be in respect to handwritten annotated
   * overly verbose.
   * </p>
   * 
   * @throws CodegenException if any exception occurs.
   */
  public void genCode() throws CodegenException {

    // check if everything is in a legal state for code generation
    checkstate();

    // Load an ontology
    try {
      ontology = loadOntology();
    } catch (OWLOntologyCreationException e) {
      throw new CodegenException("unable to open an ontology", e);
    }

    // build a JCodeModel to hold the Java representation
    jmodel = new JCodeModel();

    // add the User specified Package
    jpack = jmodel._package(javaPackageName);

    // add comments to the Package
    this.annotatePackage();

    // Interfaces and Classes
    this.addClasses();

    // Properties
    this.addProperties();

    // finish the Interfaces and create an implementation
    for (JInterfaceProxy jiface : interfaces.values()) {
      jiface.addImplementation(javaClassSuffix);
    }

    // create an object factory?
    if (createObjectFactory && objectFactoryName != null) {
      try {
        JDefinedClass factory = jpack._class(JMod.PUBLIC, objectFactoryName);
        factory.annotate(OwlRegistry.class);
        factory.javadoc().add("Lets you create Classes from an OWL Ontology programmatically.");
        // run through the interfaces with implementations
        for (JInterfaceProxy jiface : interfaces.values()) {
          jiface.addtoObjectFactory(factory);
        }
      } catch (JClassAlreadyExistsException e) {
        throw new CodegenException(
            "Unable to create an ObjectFactory with the name " + objectFactoryName, e);
      }
    }

    // Create an folder if not already existent
    try {
      if (!javaSourceFolder.exists()) {
        javaSourceFolder.mkdir();
      }
    } catch (SecurityException e) {
      throw new CodegenException("Unable to access or create the Folder: " + javaSourceFolder, e);
    }

    // Write code
    try {
      jmodel.build(javaSourceFolder);
    } catch (IOException e) {
      throw new CodegenException("Unable to write Java files to Directory: " + javaSourceFolder, e);
    }
  }

  /**
   * checks if all necessary fields are set.
   */
  private void checkstate() throws IllegalStateException {
    if (javaPackageName == null) {
      throw new IllegalStateException("Must set a Java package name!");
    }
    if (javaSourceFolder == null) {
      throw new IllegalStateException("Must set a Java source folder!");
    }
    if (ontologyIriStr == null) {
      throw new IllegalStateException("No ontology URL set!");
    }
  }

  /** Write Annotations to the Package. */
  private void annotatePackage() {
    // Base IRI
    // Comments
    final JDocComment jdoc = jpack.javadoc();
    jdoc.append("Automatically generated Package from Ontology\n");
    jdoc.append("Ontology IRI: <code>");
    jdoc.append(ontologyIriStr);
    jdoc.append("</code></br>\n");
    if (this.ontologyPhysicalIriStr != null) {
      jdoc.append("Loaded from IRI: <code>").append(ontologyPhysicalIriStr).append("</code>");
    }

    // Annotation
    jpack.annotate(OwlOntology.class).param("uri", ontologyIriStr);

    // OWL Imports
    Set<OWLImportsDeclaration> imports = ontology.getImportsDeclarations();
    if (!imports.isEmpty()) {
      JAnnotationUse annotate = jpack.annotate(OwlImports.class);
      JAnnotationArrayMember annot = annotate.paramArray("uris");
      jdoc.append("</br>\nImported Ontologies:\n<ul>");
      for (OWLImportsDeclaration imprt : imports) {
        String impIri = imprt.getIRI().toString();
        jdoc.append("\t<li><code>").append(impIri).append("</code></li>\n");
        annot.param(impIri);
      }
      jdoc.append("<ul>\n</br>\n</br>\n");
    }

    // owl annotations
    for (OWLAnnotation oannot : ontology.getAnnotations()) {
      LOG.debug("Ontology annotation {}", oannot);
      OWLAnnotationObjectVisitor v = new OWLAnnotationObjectVisitor() {

        @Override
        public void visit(OWLLiteral literal) {
          String value = literal.getLiteral();
          jdoc.add("\nOWL Annotation ");
          // jdoc.append(oannot.toString());
          jdoc.append(": ");
          jdoc.append(value);
          jdoc.append("</br>");
        }

        @Override
        public void visit(OWLAnonymousIndividual individual) {}

        @Override
        public void visit(IRI iri) {}

        @Override
        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {}

        @Override
        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationAssertionAxiom axiom) {}

        @Override
        public void visit(OWLAnnotation node) {
          String value = node.getValue().toString();
          jdoc.add("\nOWL Annotation ");
          // jdoc.append(oannot.toString());
          jdoc.append(": ");
          jdoc.append(value);
          jdoc.append("</br>");
        }
      };
      LOG.debug("Annotation: {}", oannot.toString());
      oannot.accept(v);

    }

  }

  /**
   * Reads the Classes from the Ontology and builds the corresponding Interfaces.
   */
  private void addClasses() throws CodegenException {
    /*
     * run through the classes and generate Interface Stubs Random order so we have to first
     * generate all classes and then look for the Hierarchy
     */
    boolean includeImportsClosure = true;
    Set<OWLClass> classesInSignature = ontology.getClassesInSignature(includeImportsClosure);
    for (OWLClass ocls : classesInSignature) {

      // basically OWL classes map "best" to Java Interfaces
      LOG.debug("Using OWLClass: {}", ocls);

      // Initially build the proxies to generate Implementation

      // generate with normal name?
      final String className = OntologyUtil.getClassName(ocls);
      String name = ocls.isOWLThing() ? owlthingclassname : className;
      JInterfaceProxy jinterface =
          new JInterfaceProxy(name, jpack, generateInterfaces, javaClassSuffix);

      LOG.info("Adding JInterfaceProxy for class {}", name);
      interfaces.put(name, jinterface);

      // add some Annotations
      this.annotateClass(jinterface, ocls);
    }

    Set<OWLOntology> importsClosure = ontology.getImportsClosure();

    // another run for the correct Type hierarchy and Annotation
    for (OWLClass ocls : classesInSignature) {

      // get the corresponding Java interface
      JInterfaceProxy jinterface = getInterface(ocls);

      // check for multiple inheritance
      final Set<OWLClassExpression> superClasses =
          EntitySearcher.getSuperClasses(ocls, ontology).collect(Collectors.toSet());
      if (superClasses.size() > 1 && !this.generateInterfaces) {
        throw new CodegenException("Unable to create Classes with multiple inheritance.\n"
            + "Use Interfaces via the generateInterfaces option.");
      }
      if (superClasses.size() == 0) {
        JInterfaceProxy thingInterface = interfaces.get(owlthingclassname);
        if (!thingInterface.equals(jinterface)) {
          LOG.info("Adding default subclassing of Thing for level 1 class {}", ocls.getIRI());
          // Subinterface them
          jinterface.addIntersection(thingInterface);
        }
      }

      // Type Hierarchy build Subclasses Connection
      final Set<OWLClassExpression> subClasses =
          EntitySearcher.getSubClasses(ocls, ontology).collect(Collectors.toSet());
      String superClassFullName = jinterface.fullName();
      for (OWLClassExpression odesc : subClasses) {

        final JInterfaceProxy ljinterface = getInterface(odesc);
        String fullName = ljinterface.fullName();
        LOG.debug("Java: Class {} extends {} ", fullName, superClassFullName);
        // for(OWLAxiom indi : ((OWLClass) odesc).getReferencingAxioms(ontology)){
        // System.out.println(indi);//TODO manage restriction on DataHasValue
        // }

        // Subinterface them
        ljinterface.addToImplementors(jinterface);
      }
    }

    // third Phase checking for equivalent classes
    for (final OWLClass ocls : classesInSignature) {
      final JInterfaceProxy jinterface = getInterface(ocls);

      final Set<OWLClassExpression> equivalentClasses =
          EntitySearcher.getEquivalentClasses(ocls, ontology).collect(Collectors.toSet());
      for (final OWLClassExpression odesc : equivalentClasses) {
        OWLClassExpressionVisitor vis = new OWLClassExpressionVisitor() {

          @Override
          public void visit(OWLClass desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectIntersectionOf desc) {
            LOG.debug("Class {} is IntersectionOf: ", ocls, desc.getOperands());
            for (OWLClassExpression idesc : desc.getOperands()) {
              JInterfaceProxy ljinterface = getInterface(idesc);
              if (ljinterface != null) {
                jinterface.addIntersection(ljinterface);
              } else {
                final String msg = "Nested class operands are not managed: Class " + ocls
                    + " is equivalent to "
                    + desc.toString() + " ";
                LOG.warn(msg);
                /// throw new IllegalStateException(msg);
              }

            }
          }

          @Override
          public void visit(OWLObjectUnionOf desc) {
            LOG.debug("Class {} is UnionOf: {} ", ocls, desc.getOperands());
            for (OWLClassExpression idesc : desc.getOperands()) {
              JInterfaceProxy ljinterface = getInterface(idesc);
              jinterface.addUnion(ljinterface);
            }
          }

          @Override
          public void visit(OWLObjectComplementOf desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectOneOf desc) {
            LOG.debug("Individuals: {}", desc.getIndividuals());
          }

          @Override
          public void visit(OWLObjectSomeValuesFrom desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectAllValuesFrom desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectHasValue desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectMinCardinality desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectExactCardinality desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectMaxCardinality desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLObjectHasSelf desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLDataSomeValuesFrom desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLDataAllValuesFrom desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLDataHasValue desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLDataMinCardinality desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLDataExactCardinality desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

          @Override
          public void visit(OWLDataMaxCardinality desc) {
            LOG.warn("Not yet able to handle equivalent classes on {} eq {}", ocls, desc);
          }

        };

        try {
          odesc.accept(vis);
        } catch (IllegalStateException e) {
          throw new CodegenException(e);
        }

      }
    }
  }

  private JInterfaceProxy getInterface(OWLClassExpression desc) {
    if (desc.isOWLThing()) {
      return interfaces.get(owlthingclassname);
    }
    if (desc.isOWLClass()) {
      final OWLClass ocls = desc.asOWLClass();
      final String className = OntologyUtil.getClassName(ocls);
      return interfaces.get(className);
    }
    LOG.warn("OWLClassExpression {} not managed", desc);
    return null;

  }

  private void addProperties() {

    // Add Id Property?
    if (generateIdField) {
      Property prop = new Property();
      prop.setPtype(PropertyType.ID);
      prop.setBaseType(jmodel._ref(String.class));
      prop.setFunctional(true);
      prop.setName(this.idFieldName);
      this.interfaces.get(this.owlthingclassname).addProperty(prop);
    }

    final boolean importsClosure = true;
    // Data Properties
    for (OWLDataProperty dprop : ontology.getDataPropertiesInSignature(importsClosure)) {
      this.addProperties(dprop, PropertyType.DATA);
    }

    // Object Properties
    for (OWLObjectProperty oprop : ontology.getObjectPropertiesInSignature(importsClosure)) {
      this.addProperties(oprop, PropertyType.OBJECT);
    }

  }

  private void addProperties(OWLProperty prop, PropertyType type) {
    LOG.debug("Property: {} \t {}", prop.getClass(), prop);
    if (ignoreProperties.contains(prop.getIRI().toString())) {
      LOG.warn("\t\t Ignoring blacklisted property {}", prop.getIRI().toString());
      return;
    }
    Set<OWLClassExpression> domains = null;
    if (prop instanceof OWLDataProperty) {
      domains =
          EntitySearcher.getDomains((OWLDataProperty) prop, ontology).collect(Collectors.toSet());
    } else if (prop instanceof OWLObjectProperty) {
      domains =
          EntitySearcher.getDomains((OWLObjectProperty) prop, ontology).collect(Collectors.toSet());
    }
    if (domains == null || domains.isEmpty()) {
      // if it is not associated with a special class than it can be used at owl:Thing level
      LOG.warn("\t\t Property {} has empty domain; bounding to default class (Thing)",
          prop.getIRI());
      this.addProperty(prop, interfaces.get(owlthingclassname), type);
    } else {
      // for each included class Methods have to be generated
      for (OWLClassExpression odes : domains) {
        LOG.trace("\t\t Domain: {}", odes);

        // all associated classes will be added
        for (OWLClass ocls : OntologyUtil.getOwlClasses(odes)) {
          // add a property
          final String className = OntologyUtil.getClassName(ocls);
          JInterfaceProxy iface = interfaces.get(className);
          this.addProperty(prop, iface, type);
        }
      }
    }
  }

  private void addProperty(OWLProperty prop, JInterfaceProxy iface, PropertyType type) {

    // adding a new Property
    final Property jprop = new Property();

    // variable name in normal case and first letter uppercase
    final String fragment = prop.getIRI().getFragment();
    final StringBuilder propNameStringBuilder = new StringBuilder(fragment.toString());
    propNameStringBuilder.setCharAt(0, Character.toLowerCase(propNameStringBuilder.charAt(0)));
    final String propName = toCamelCase(propNameStringBuilder.toString());
    jprop.setName(propName);

    // Property Type
    jprop.setPtype(type);

    // Functional?
    boolean functional = false;
    Set<? extends OWLPropertyRange> ranges = null;
    if (prop instanceof OWLDataProperty) {
      final OWLDataProperty dataProp = (OWLDataProperty) prop;
      functional = EntitySearcher.isFunctional(dataProp, ontology.getImportsClosure().stream());
      ranges = EntitySearcher.getRanges(dataProp, ontology).collect(Collectors.toSet());
      // has this Property a Range? look first if overridden (works up 2 levels..)
      if (ranges.isEmpty()) {
        ranges = EntitySearcher.getRanges(dataProp, ontology.getDirectImports().stream())
            .collect(Collectors.toSet());
      }
      if (ranges.isEmpty()) {
        ranges = EntitySearcher.getRanges(dataProp, ontology.getImportsClosure().stream())
            .collect(Collectors.toSet());
      }
    } else if (prop instanceof OWLObjectProperty) {
      final OWLObjectProperty objProp = (OWLObjectProperty) prop;
      functional = EntitySearcher.isFunctional(objProp, ontology.getImportsClosure().stream());
      ranges = EntitySearcher.getRanges(objProp, ontology).collect(Collectors.toSet());
      // has this Property a Range? look first if overridden (works up 2 levels..)
      if (ranges.isEmpty()) {
        ranges = EntitySearcher.getRanges(objProp, ontology.getDirectImports().stream())
            .collect(Collectors.toSet());
      }
      if (ranges.isEmpty()) {
        ranges = EntitySearcher.getRanges(objProp, ontology.getImportsClosure().stream())
            .collect(Collectors.toSet());
      }
    }

    jprop.setFunctional(functional);
    LOG.debug("  Adding Property {} [{}]{} to Class {}",
        new Object[] {propName, prop.getIRI(), (functional ? "*" : ""), iface.name()});

    // Property IRI
    jprop.setPropUri(prop.getIRI().toURI());


    // if there is no Range we can save some time
    if (ranges != null && !ranges.isEmpty()) {
      // if it has more than one data Range than Make it a String
      // still then you are on your own in specifying the correct Syntax
      if (ranges.size() > 1 && prop instanceof OWLDataProperty) {
        LOG.warn("  Range of Property '{}' is bigger than one: {}", prop, ranges);
        // using string as fall back
        jprop.setBaseType(jmodel._ref(String.class));
        iface.addProperty(jprop);
      } else {
        // Set Data type to use for the Functions
        if (prop instanceof OWLDataProperty) {
          // should be now only one for OWLDataProperty
          OWLPropertyRange next = ranges.iterator().next();
          jprop.setBaseType(this.switchType(next));
        } else if (prop instanceof OWLObjectProperty) {
          if (ranges.size() > 1 || ranges.size() == 0) {
            // more than one Class can be Included
            LOG.info("  Workaround using Object for Prop {} with Ranges: {}", prop, ranges);
            jprop.setBaseType(jmodel._ref(Object.class));
            // could also use thing here??
          } else {
            OWLPropertyRange pr = ranges.iterator().next();
            if (pr instanceof OWLClass) {
              //
              IRI propIri = ((OWLClass) pr).getIRI();
              jprop.setBaseType(interfaces.get(propIri.getFragment().toString()));
              // jtype = jpack._getClass(((OWLClass)pr).toString());
            } else {
              jprop.setBaseType(jmodel._ref(Object.class));
              LOG.warn("Unable to handle this Construct: {}", pr);
              LogUtil.logObjectInfo(pr, LOG);
            }
          }
        } else {
          LOG.error(" Encoutered Unknown Property: {}", prop);
          throw new IllegalStateException(" Encoutered Unknown Property: " + prop);
        }
        iface.addProperty(jprop);
      }

      // walk through the ranges an annotate
      for (OWLPropertyRange o : ranges) {
        if (o instanceof OWLDatatype) {
          // DataProperty data type IRI
          jprop.addDatatype(((OWLDatatype) o).getIRI().toURI());
        } else if (o instanceof OWLClass) {
          // ObjectProperty Class Data type IRI
          jprop.addDatatype(((OWLClass) o).getIRI().toURI());
        } else {
          LOG.warn("  Unable to annotate this construct: {} of Type {} ", o,
              o.getClass().getName());
          LogUtil.logObjectInfo(o, LOG);
        }
      }
    }

    final List<OWLAnnotation> annotations =
        EntitySearcher.getAnnotations(prop, ontology).collect(Collectors.toList());
    for (OWLAnnotation oa : annotations) {
      OWLAnnotationObjectVisitor v = new OWLAnnotationObjectVisitor() {

        @Override
        public void visit(OWLLiteral literal) {
          jprop.addComment(literal.getLiteral());
        }

        @Override
        public void visit(OWLAnonymousIndividual individual) {}

        @Override
        public void visit(IRI iri) {}

        @Override
        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {}

        @Override
        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationAssertionAxiom axiom) {}

        @Override
        public void visit(OWLAnnotation node) {
          if (node.getProperty().isComment()) {
            OWLAnnotationValue value = node.getValue();
            if (value instanceof OWLLiteral) {
              OWLLiteral l = (OWLLiteral) value;
              jprop.addComment(l.getLiteral());
            }
          }
        }
      };
      LOG.debug("Found annotation: {}", oa.toString());
      oa.accept(v);
    }
  }

  private String toCamelCase(String value) {
    StringBuilder sb = new StringBuilder();
    value = value.replaceAll("_", "-");
    final char delimChar = '-';
    boolean upperCaseNext = false;
    for (int charInd = 0; charInd < value.length(); ++charInd) {
      char valueChar = value.charAt(charInd);
      if (valueChar == delimChar) {
        // do not add the '-' char
        upperCaseNext = true;
      } else {
        if (upperCaseNext) {
          valueChar = Character.toUpperCase(valueChar);
          upperCaseNext = false;
        }
        sb.append(valueChar);
      }
    }
    return sb.toString();
  }

  private Class<?> switchDataType(OWLPropertyRange range) {
    Class<?> result = null;
    if (range instanceof OWLDatatype) {
      OWLDatatype dt = (OWLDatatype) range;
      XsdType fromIri = XsdType.fromIri(dt.getIRI());
      result = typeMapper.getType(fromIri);
      LOG.debug("DataType: {} OwlType: {}", result, dt);
    } else if (range instanceof OWLDataOneOf) {
      OWLDataOneOf dt = (OWLDataOneOf) range;
      result = Object.class;
      LOG.debug("DataType: {} OwlType: {}", result, dt);
    } else {
      LOG.warn("Unable to handle this Object {}", range.toString());
      LogUtil.logObjectInfo(range, LOG);
    }
    return result;
  }

  /**
   * Convenience method returning a JType.
   */
  private JClass switchType(OWLPropertyRange range) {
    Class<?> dt = switchDataType(range);

    if (dt == null) {
      // TODO:
    }

    return jmodel.ref(dt);
  }

  /**
   * Add some Annotations to the Interface.
   */
  private void annotateClass(final JInterfaceProxy iface, OWLClass ocls) {

    iface.setClassUri(ocls.getIRI().toURI());

    // get further Annotation from the Ontology for the current class

    final List<OWLAnnotation> annotations =
        EntitySearcher.getAnnotations(ocls, ontology).collect(Collectors.toList());
    for (final OWLAnnotation oannot : annotations) {
      OWLAnnotationObjectVisitor v = new OWLAnnotationObjectVisitor() {

        @Override
        public void visit(OWLLiteral literal) {

          // add annotation to the Interface
          // add the value pairs
          String content = literal.getLiteral();
          String dturi = literal.getDatatype().getIRI().toString();
          IRI iri = oannot.getProperty().getIRI();

          // add the annotation
          iface.addAnnotation(iri.toURI(), content, dturi, false);
        }

        @Override
        public void visit(OWLAnonymousIndividual individual) {}

        @Override
        public void visit(IRI iri) {}

        @Override
        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {}

        @Override
        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {}

        @Override
        public void visit(OWLAnnotationAssertionAxiom axiom) {}

        @Override
        public void visit(OWLAnnotation node) {

          // add annotation to the Interface
          // add the value pairs
          String content = node.getValue().toString();
          // String dturi = literal.getDatatype().getIRI().toString();
          IRI iri = oannot.getProperty().getIRI();
          // add the annotation
          boolean isComment = node.getProperty().isComment();
          String dturi = node.getProperty().getIRI().toString();
          iface.addAnnotation(iri.toURI(), content, dturi, isComment);
        }
      };
      LOG.debug("Annotation: {}", oannot.toString());
      oannot.accept(v);
    }
  }

  /**
   * Sets the name of the Java Package to be created for the ontology.
   * 
   * @param packageName the package name
   */
  public void setJavaPackageName(String packageName) {
    if (packageName != null && (packageName.length() >= 0)
        && JJavaName.isJavaPackageName(packageName)) {
      this.javaPackageName = packageName;
    } else {
      throw new IllegalArgumentException("Invalid Java Package Name");
    }
  }

  public String getJavaPackageName() {
    return this.javaPackageName;
  }

  /**
   * Returns the ontologyIri.
   */
  public String getOntologyIri() {
    return ontologyIriStr;
  }

  /**
   * Sets the ontology IRI.
   * 
   * @param ontologyIri the ontologyIri to set
   */
  public void setOntologyIri(String ontologyIri) {
    // if this uri is not correct throw an exception
    IRI.create(ontologyIri);
    this.ontologyIriStr = ontologyIri;
  }

  /**
   * Returns the javaSourceFolder.
   */
  public File getJavaSourceFolder() {
    return javaSourceFolder;
  }

  /**
   * Returns javaSourceFolder the javaSourceFolder to set.
   */
  public void setJavaSourceFolder(File javaSourceFolder) {
    this.javaSourceFolder = javaSourceFolder;
  }

  /**
   * Returns the javaClassSuffix.
   */
  public String getJavaClassSuffix() {
    return javaClassSuffix;
  }

  /**
   * Set the Suffix for the Name of the generated Classes. Default is "Impl". E.g. having an Owl
   * Class named Pizza would result in an {@code interface Pizza} and an class
   * {@code class PizzaImpl implements Pizza}.
   * 
   * @param javaClassSuffix the javaClassSuffix to set
   */
  public void setJavaClassSuffix(String javaClassSuffix) {
    if (javaClassSuffix == null) {
      this.javaClassSuffix = "";
    } else {
      this.javaClassSuffix = javaClassSuffix;
    }
  }

  /**
   * Returns the ontologyPhysicalIri.
   */
  public String getOntologyPhysicalIri() {
    return ontologyPhysicalIriStr;
  }

  /**
   * Sets the physical iri of the ontology.
   * 
   * @param ontologyPhysicalIri the ontologyPhysicalIri to set
   */
  public void setOntologyPhysicalIri(String ontologyPhysicalIri) {
    // if this uri is not correct throw an exception
    IRI.create(ontologyPhysicalIri);
    this.ontologyPhysicalIriStr = ontologyPhysicalIri;
  }

  /**
   * Returns {@code true} if Interfaces will be generated.
   */
  public boolean isGenerateInterfaces() {
    return generateInterfaces;
  }

  /**
   * Tells the codegen to generate Interfaces and Implementations thereof. If {@code false} then
   * only implementations (=Java Classes) will be generated, if this is impossible, because of
   * multiple class inheritance in the ontology a runtime error will be thrown during execution.
   * <br/>
   * The default value is {@code true}.
   * 
   * @param generateInterfaces if interfaces should be generated
   */
  public void setGenerateInterfaces(boolean generateInterfaces) {
    this.generateInterfaces = generateInterfaces;
  }

  /**
   * Returns {@code true} if Id Fields will be generated.
   */
  public boolean isGenerateIdField() {
    return generateIdField;
  }

  /**
   * Tells the codegen to generate ID Fields for the Individual name (rdf:about, rdf:id or the like)
   * for all generated classes <br />
   * Default value is <code>false</code>.
   * 
   * @param generateIdField if ID fields should be generated
   * @see #setIdFieldName(String)
   */
  public void setGenerateIdField(boolean generateIdField) {
    this.generateIdField = generateIdField;
  }

  /**
   * Returns the name of the generated ID field.
   */
  public String getIdFieldName() {
    return idFieldName;
  }

  /**
   * Sets the name of the owl Individual id field in the generated Java classes.<br/>
   * Default value is <code>"name"</code>.
   * 
   * @param idFieldName the id field name
   * @see #setGenerateIdField(boolean)
   */
  public void setIdFieldName(String idFieldName) {
    this.idFieldName = idFieldName;
  }

  /**
   * Returns if a ObjectFactory will be generated.
   */
  public boolean isCreateObjectFactory() {
    return createObjectFactory;
  }

  /**
   * Tells the codegen whether an ObjectFactory shall be generated or not. The ObjectFactory will
   * hold all the necessary methods to generate new Instances of the Class defined in the Ontology
   * programmatically.<br/>
   * Default value is <code>true</code>.
   * 
   * @param createObjectFactory the createObjectFactory to set
   * @see #setObjectFactoryName(String)
   */
  public void setCreateObjectFactory(boolean createObjectFactory) {
    this.createObjectFactory = createObjectFactory;
  }

  /**
   * Returns the objectFactoryName.
   */
  public String getObjectFactoryName() {
    return objectFactoryName;
  }

  /**
   * Sets the name of the ObjectFactory Java class.<br>
   * Default value is <code>"ObjectFactory"</code>.
   * 
   * @param objectFactoryName the objectFactoryName to set
   * @see #setCreateObjectFactory(boolean)
   */
  public void setObjectFactoryName(String objectFactoryName) {
    this.objectFactoryName = objectFactoryName;
  }

  public void setIgnoreProperties(List<String> ignoreProperties) {
    this.ignoreProperties = ignoreProperties;
  }

  public void setIgnoredAbstractClassIris(List<String> ignoredAbstractClassIris) {
    Codegen.ignoredAbstractClassIris.addAll(ignoredAbstractClassIris);
  }
}
