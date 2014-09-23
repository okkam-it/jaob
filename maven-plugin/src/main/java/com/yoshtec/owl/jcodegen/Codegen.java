package com.yoshtec.owl.jcodegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * This Class generates iCode out of an ontology, where most of the declared
 * classes will result in a Java class and property declarations will be added
 * to them with the appropriate setter and getter methods. </br> <h2>Example:</h2>
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
 * 
 * @author Jonas von Malottki
 * @author Flavio Pompermaier
 * 
 */

public class Codegen {

	/** Logger */
	static private final Logger log = LoggerFactory.getLogger(Codegen.class);

	// Settings

	/** logical IRI of the Ontology */
	private String ontologyIri = null;

	/** physical IRI of the Ontology */
	private String ontologyPhysicalIri = null;

	/** Java Package name to be created */
	private String javaPackageName = null;

	/** source folder to put generated classes in */
	private File javaSourceFolder = null;

	/** Suffix for an of an Interface */
	private String javaClassSuffix = "Impl";

	/** Class Name for the owl:thing Class */
	private String owlthingclassname = "Thing";

	/** whether a Object Factory Class should be generated */
	private boolean createObjectFactory = true;

	/** name of the Object Factory Class */
	private String objectFactoryName = "ObjectFactory";

	/** if a OWL ID Property shall be generated */
	private boolean generateIdField = false;

	/** name of the OWL ID field / property */
	private String idFieldName = "name";

	/** if interfaces should be generated */
	private boolean generateInterfaces = true;

	// local work variables
	private OWLOntology ontology = null;
	private JCodeModel jmodel = null;
	private JPackage jpack = null;

	private final XsdTypeMapper typeMapper = new XsdTypeMapper();

	/** holds the interfaces and the Properties */
	private Map<String, JInterfaceProxy> interfaces = new HashMap<String, JInterfaceProxy>();

	
	private List<String> ignoreProperties = new ArrayList<>();

	/**
	 * Creates a new Code Generator.
	 */
	public Codegen() {
	}

	/**
	 * Loads an Ontology
	 * 
	 * @return
	 * @throws OWLOntologyCreationException
	 * @throws Exception
	 */
	private OWLOntology loadOntology() throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology result = null;

		IRI ontologyIRI = IRI.create(ontologyIri);

		if (ontologyPhysicalIri != null && ontologyIri != null) {
			// Create a physical IRI which can be resolved to point to where our
			// ontology will be saved.
			IRI physicalIRI = IRI.create(ontologyPhysicalIri);

			// Set up a mapping, which maps the ontology IRI to the physical IRI
			SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, physicalIRI);
			manager.addIRIMapper(mapper);
			// manager.addIRIMapper(new NonMappingOntologyIRIMapper());

			log.debug(
					"Attempting to load Ontology (IRI={}) from physical IRI={}",
					ontologyIRI, physicalIRI);

			result = manager.loadOntology(physicalIRI);

		} else {
			log.debug("Attempting to load Ontology from IRI={}", ontologyIRI);
			result = manager.loadOntology(ontologyIRI);
		}

		log.info("Successfully loaded Ontology IRI={}", ontologyIRI);

		return result;

	}

	/**
	 * Generates java code for a given Ontology.
	 * 
	 * The Java code makes heavily use of Annotations. And will be in respect to
	 * handwritten annotated overly verbose.
	 * 
	 * @throws CodegenException
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
				JDefinedClass factory = jpack._class(JMod.PUBLIC,
						objectFactoryName);
				factory.annotate(OwlRegistry.class);
				factory.javadoc()
						.add("Lets you create Classes from an OWL Ontology programmatically.");
				// run through the interfaces with implementations
				for (JInterfaceProxy jiface : interfaces.values()) {
					jiface.addtoObjectFactory(factory);
				}
			} catch (JClassAlreadyExistsException e) {
				throw new CodegenException(
						"Unable to create an ObjectFactory with the name "
								+ objectFactoryName, e);
			}
		}

		// Create an folder if not already existent
		try {
			if (!javaSourceFolder.exists()) {
				javaSourceFolder.mkdir();
			}
		} catch (SecurityException e) {
			throw new CodegenException(
					"Unable to access or create the Folder: "
							+ javaSourceFolder, e);
		}

		// Write code
		try {
			jmodel.build(javaSourceFolder);
		} catch (IOException e) {
			throw new CodegenException(
					"Unable to write Java files to Directory: "
							+ javaSourceFolder, e);
		}
	}

	/**
	 * checks if all necessary fields are set.
	 */
	private void checkstate() throws IllegalStateException {
		if (javaPackageName == null)
			throw new IllegalStateException("Must set a Java package name!");
		if (javaSourceFolder == null)
			throw new IllegalStateException("Must set a Java source folder!");
		if (ontologyIri == null)
			throw new IllegalStateException("No ontology URL set!");
	}

	/**
	 * Write Annotations to the Package
	 */
	private void annotatePackage() {
		// Base IRI
		// Comments
		final JDocComment jdoc = jpack.javadoc();
		jdoc.append("Automatically generated Package from Ontology\n");
		jdoc.append("Ontology IRI: <code>");
		jdoc.append(ontologyIri);
		jdoc.append("</code></br>\n");
		if (this.ontologyPhysicalIri != null)
			jdoc.append("Loaded from IRI: <code>").append(ontologyPhysicalIri)
					.append("</code>");

		// Annotation
		jpack.annotate(OwlOntology.class).param("uri", ontologyIri);

		// OWL Imports
		Set<OWLImportsDeclaration> imports = ontology.getImportsDeclarations();
		if (!imports.isEmpty()) {
			JAnnotationUse annotate = jpack.annotate(OwlImports.class);
			JAnnotationArrayMember annot = annotate.paramArray("uris");
			jdoc.append("</br>\nImported Ontologies:\n<ul>");
			for (OWLImportsDeclaration imprt : imports) {
				String impIri = imprt.getIRI().toString();
				jdoc.append("\t<li><code>")
					.append(impIri)
					.append("</code></li>\n");
				annot.param(impIri);
			}
			jdoc.append("<ul>\n</br>\n</br>\n");
		}

		// owl annotations
		for (OWLAnnotation oannot : ontology.getAnnotations()) {
			log.debug("Ontology annotation {}", oannot);
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
				public void visit(OWLAnonymousIndividual individual) {
				}

				@Override
				public void visit(IRI iri) {
				}

				@Override
				public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
				}

				@Override
				public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
				}

				@Override
				public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
				}

				@Override
				public void visit(OWLAnnotationAssertionAxiom axiom) {
				}

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
			log.debug("Annotation: {}", oannot.toString());
			oannot.accept(v);

		}

	}

	/**
	 * Reads the Classes from the Ontology and builds the corresponding
	 * Interfaces.
	 */
	private void addClasses() throws CodegenException {
		/*
		 * run through the classes and generate Interface Stubs Random order so
		 * we have to first generate all classes and then look for the Hierarchy
		 */
		boolean includeImportsClosure = true;
		Set<OWLClass> classesInSignature = ontology
				.getClassesInSignature(includeImportsClosure);
		for (OWLClass ocls : classesInSignature) {

			// basically OWL classes map "best" to Java Interfaces
			log.debug("Using OWLClass: {}", ocls);

			// Initially build the proxies to generate Implementation

			// generate with normal name?
			String classFragment = ocls.getIRI().getFragment().toString();
			String name = ocls.isOWLThing() ? owlthingclassname : classFragment;
			JInterfaceProxy jinterface = new JInterfaceProxy(name, jpack,
					generateInterfaces, javaClassSuffix);

			log.info("Adding JInterfaceProxy for class {}", name);
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
			if (ocls.getSuperClasses(ontology).size() > 1
					&& !this.generateInterfaces) {
				throw new CodegenException(
						"Unable to create Classes with multiple inheritance. \n Use Interfaces via the generateInterfaces option.");
			}

			// Type Hierarchy build Subclasses Connection
			Set<OWLClassExpression> subClasses = ocls.getSubClasses(importsClosure);
			String superClassFullName = jinterface.fullName();
			for (OWLClassExpression odesc : subClasses) {

				JInterfaceProxy ljinterface = getInterface(odesc);
				String fullName = ljinterface.fullName();
				log.debug("Java: Class {} extends {} ", fullName, superClassFullName);

				// Subinterface them
				ljinterface._implements(jinterface);
			}
		}

		// third Phase checking for equivalent classes
		for (final OWLClass ocls : classesInSignature) {
			final JInterfaceProxy jinterface = getInterface(ocls);

			Set<OWLClassExpression> equivalentClasses = ocls.getEquivalentClasses(importsClosure);
			for (final OWLClassExpression odesc : equivalentClasses) {
				OWLClassExpressionVisitor vis = new OWLClassExpressionVisitor() {

					@Override
					public void visit(OWLClass desc) {
						log.warn("Not yet able to handle equivalent classes on {} eq {}",ocls, desc);
					}

					@Override
					public void visit(OWLObjectIntersectionOf desc) {
						log.debug("Class {} is IntersectionOf: ", ocls,
								desc.getOperands());
						for (OWLClassExpression idesc : desc.getOperands()) {
							JInterfaceProxy ljinterface = getInterface(idesc);
							if (ljinterface != null) {
								jinterface.addIntersection(ljinterface);
							} else {
								final String msg = "Nested class Operands: Class "
										+ ocls
										+ " is equivalent to "
										+ desc.toString() + " ";
								log.debug(msg);
								throw new IllegalStateException(msg);
							}

						}
					}

					@Override
					public void visit(OWLObjectUnionOf desc) {
						log.debug("Class {} is UnionOf: {} ", ocls,
								desc.getOperands());
						for (OWLClassExpression idesc : desc.getOperands()) {
							JInterfaceProxy ljinterface = getInterface(idesc);
							jinterface.addUnion(ljinterface);
						}
					}

					@Override
					public void visit(OWLObjectComplementOf desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLObjectOneOf desc) {
						log.debug("Individuals: {}", desc.getIndividuals());
					}

					@Override
					public void visit(OWLObjectSomeValuesFrom desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLObjectAllValuesFrom desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLObjectHasValue desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLObjectMinCardinality desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLObjectExactCardinality desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLObjectMaxCardinality desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLObjectHasSelf desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLDataSomeValuesFrom desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLDataAllValuesFrom desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLDataHasValue desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLDataMinCardinality desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLDataExactCardinality desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
					}

					@Override
					public void visit(OWLDataMaxCardinality desc) {
						log.warn(
								"Not yet able to handle equivalent classes on {} eq {}",
								ocls, desc);
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
		try {
			// get the corresponding Java interface
			OWLClass ocls = desc.asOWLClass();
			String classFragment = ocls	.getIRI().getFragment().toString();
			String name = (ocls.isOWLThing() ? owlthingclassname : classFragment);
			return interfaces.get(name);
		} catch (OWLRuntimeException e) {
			log.debug("Found anonymous or strange class {}", desc);
		}
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

			for (JInterfaceProxy jiface : interfaces.values()) {
				jiface.addProperty(prop);
			}

			this.interfaces.get(this.owlthingclassname).addProperty(prop);
		}

		boolean importsClosure = true;
		// Data Properties
		Set<OWLDataProperty> dataProperties = ontology.getDataPropertiesInSignature(importsClosure);
		for (OWLProperty<?, ?> dprop : dataProperties) {
			this.addProperties(dprop, PropertyType.DATA);
		}

		// Object Properties
		Set<OWLObjectProperty> objectProperties = ontology.getObjectPropertiesInSignature(importsClosure);
		for (OWLProperty<?, ?> oprop : objectProperties) {
			this.addProperties(oprop, PropertyType.OBJECT);
		}

	}

	private void addProperties(OWLProperty<?, ?> prop, PropertyType type) {

		log.debug("Property: {} \t {}", prop.getClass(), prop);
		if(ignoreProperties.contains(prop.getIRI().toString())){
			log.warn("\t\t Ignoring blacklisted property {}",prop.getIRI().toString());
			return;
		}

		Set<OWLClassExpression> domains = prop.getDomains(ontology.getImportsClosure());
		if (domains.isEmpty()) {
			// if it is not associated with a special class than it can be used at owl:Thing level
			log.warn("\t\t Property {} has empty domain; bounding to default class (Thing)", prop.getIRI());
			this.addProperty(prop, interfaces.get(owlthingclassname), type);
		} else {
			// for each included class Methods have to be generated
			for (OWLClassExpression odes : domains) {

				log.trace("\t\t Domain: {}", odes);

				// all associated classes will be added
				for (OWLClass ocls : OntologyUtil.getOWLClasses(odes)) {
					// add a property
					String fragment = ocls.getIRI().getFragment().toString();
					JInterfaceProxy iface = interfaces.get(fragment);
					this.addProperty(prop, iface, type);
				}
			}
		}
	}

	private void addProperty(OWLProperty<?, ?> prop, JInterfaceProxy iface,
			PropertyType type) {

		// adding a new Property
		final Property jprop = new Property();

		// variable name in normal case and first letter uppercase
		String fragment = prop.getIRI().getFragment();
		StringBuffer pName = new StringBuffer(fragment.toString()); 
		pName.setCharAt(0, Character.toLowerCase(pName.charAt(0)));
		String propName = toCamelCase(pName.toString());
		jprop.setName(propName);

		// Property Type
		jprop.setPtype(type);

		// Functional?
		boolean functional = prop.isFunctional(ontology);
		jprop.setFunctional(functional);
		log.debug("  Adding Property {} [{}]{} to Class {}", new Object[] {propName, prop.getIRI(),
				(functional ? "*" : ""), iface.name() });

		// Property IRI
		jprop.setPropUri(prop.getIRI().toURI());

		// has this Property a Range? look first if overridden
		Set<? extends OWLPropertyRange> ranges = prop.getRanges(ontology);
		if (ranges.isEmpty()) {
			ranges = prop.getRanges(ontology.getImportsClosure());
		}
		// if there is no Range we can save some time
		if (!ranges.isEmpty()) {

			// if it has more than one data Range than Make it a String
			// still then you are on your own in specifying the correct Syntax
			if (ranges.size() > 1 && prop instanceof OWLDataProperty) {
				log.warn("  Range of Property '{}' is bigger than one: {}",
						prop, ranges);
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
						log.info(
								"  Workaround using Object for Prop {} with Ranges: {}",
								prop, ranges);
						jprop.setBaseType(jmodel._ref(Object.class));
						// could also use thing here??
					} else {
						OWLPropertyRange pr = ranges.iterator().next();

						if (pr instanceof OWLClass) {
							//
							IRI propIri = ((OWLClass) pr).getIRI();
							jprop.setBaseType(interfaces.get(propIri
									.getFragment().toString()));
							// jtype =
							// jpack._getClass(((OWLClass)pr).toString());
						} else {
							jprop.setBaseType(jmodel._ref(Object.class));
							log.warn("Unable to handle this Construct: {}", pr);
							LogUtil.logObjectInfo(pr, log);
						}
					}
				} else {
					log.error(" Encoutered Unknown Property: {}", prop);
					throw new IllegalStateException(
							" Encoutered Unknown Property: " + prop);
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
					log.warn(
							"  Unable to annotate this construct: {} of Type {} ",
							o, o.getClass().getName());
					LogUtil.logObjectInfo(o, log);
				}
			}
		}

		for (OWLAnnotation oa : prop.getAnnotations(ontology)) {
			OWLAnnotationObjectVisitor v = new OWLAnnotationObjectVisitor() {

				@Override
				public void visit(OWLLiteral literal) {
					jprop.addComment(literal.getLiteral());
				}

				@Override
				public void visit(OWLAnonymousIndividual individual) {
				}

				@Override
				public void visit(IRI iri) {
				}

				@Override
				public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
				}

				@Override
				public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
				}

				@Override
				public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
				}

				@Override
				public void visit(OWLAnnotationAssertionAxiom axiom) {
				}

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
			log.debug("Found annotation: {}", oa.toString());
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
				//do not add the '-' char
				upperCaseNext = true;
			}else{
				if(upperCaseNext){
					valueChar = Character.toUpperCase(valueChar);
					upperCaseNext = false;
				}
				sb.append(valueChar);
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param range
	 * @return
	 */
	private Class<?> switchDataType(OWLPropertyRange range) {
		Class<?> result = null;
		if (range instanceof OWLDatatype) {
			OWLDatatype dt = (OWLDatatype) range;
			XsdType fromIri = XsdType.fromIri(dt.getIRI());
			result = typeMapper.getType(fromIri);
			log.debug("DataType: {} OwlType: {}", result, dt);
		} else if (range instanceof OWLDataOneOf) {
			OWLDataOneOf dt = (OWLDataOneOf) range;
			result = Object.class;
			log.debug("DataType: {} OwlType: {}", result, dt);
		} else {
			log.warn("Unable to handle this Object {}", range.toString());
			LogUtil.logObjectInfo(range, log);
		}
		return result;
	}

	/**
	 * Convenience method returning a JType.
	 * 
	 * @param range
	 * @return
	 */
	private JClass switchType(OWLPropertyRange range) {
		Class<?> dt = switchDataType(range);

		if (dt == null) {
			// TODO:
		}

		return jmodel.ref(dt);
	}

	/**
	 * Add some Annotations to the Interface
	 * 
	 * @param jinterface
	 * @param ocls
	 */
	private void annotateClass(final JInterfaceProxy iface, OWLClass ocls) {

		iface.setClassUri(ocls.getIRI().toURI());

		// get further Annotation from the Ontology for the current class

		for (final OWLAnnotation oannot : ocls.getAnnotations(ontology)) {
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
				public void visit(OWLAnonymousIndividual individual) {
				}

				@Override
				public void visit(IRI iri) {
				}

				@Override
				public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
				}

				@Override
				public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
				}

				@Override
				public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
				}

				@Override
				public void visit(OWLAnnotationAssertionAxiom axiom) {
				}

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
			log.debug("Annotation: {}", oannot.toString());
			oannot.accept(v);
		}
	}

	/**
	 * Sets the name of the Java Package to be created for the ontology.
	 * 
	 * @param packageName
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
	 * @return the ontologyIri
	 */
	public String getOntologyIri() {
		return ontologyIri;
	}

	/**
	 * @param ontologyIri
	 *            the ontologyIri to set
	 */
	public void setOntologyIri(String ontologyIri) {
		// if this uri is not correct throw an exception
		IRI.create(ontologyIri);
		this.ontologyIri = ontologyIri;
	}

	/**
	 * @return the javaSourceFolder
	 */
	public File getJavaSourceFolder() {
		return javaSourceFolder;
	}

	/**
	 * @param javaSourceFolder
	 *            the javaSourceFolder to set
	 */
	public void setJavaSourceFolder(File javaSourceFolder) {
		this.javaSourceFolder = javaSourceFolder;
	}

	/**
	 * @return the javaClassSuffix
	 */
	public String getJavaClassSuffix() {
		return javaClassSuffix;
	}

	/**
	 * Set the Suffix for the Name of the generated Classes. Default is "Impl".
	 * E.g. having an Owl Class named Pizza would result in an
	 * {@code interface Pizza} and an class
	 * {@code class PizzaImpl implements Pizza}.
	 * 
	 * @param javaClassSuffix
	 *            the javaClassSuffix to set
	 */
	public void setJavaClassSuffix(String javaClassSuffix) {
		if (javaClassSuffix == null) {
			this.javaClassSuffix = "";
		} else {
			this.javaClassSuffix = javaClassSuffix;
		}
	}

	/**
	 * @return the ontologyPhysicalIri
	 */
	public String getOntologyPhysicalIri() {
		return ontologyPhysicalIri;
	}

	/**
	 * @param ontologyPhysicalIri
	 *            the ontologyPhysicalIri to set
	 */
	public void setOntologyPhysicalIri(String ontologyPhysicalIri) {
		// if this uri is not correct throw an exception
		IRI.create(ontologyPhysicalIri);
		this.ontologyPhysicalIri = ontologyPhysicalIri;
	}

	/**
	 * @return {@code true} if Interfaces will be generated
	 */
	public boolean isGenerateInterfaces() {
		return generateInterfaces;
	}

	/**
	 * Tells the codegen to generate Interfaces and Implementations thereof. If
	 * {@code false} then only implementations (=Java Classes) will be
	 * generated, if this is impossible, because of multiple class inheritance
	 * in the ontology a runtime error will be thrown during execution. <br/>
	 * The default value is {@code true}.
	 * 
	 * @param generateInterfaces
	 *            if interfaces should be generated
	 */
	public void setGenerateInterfaces(boolean generateInterfaces) {
		this.generateInterfaces = generateInterfaces;
	}

	/**
	 * @return {@code true} if Id Fields will be generated
	 */
	public boolean isGenerateIdField() {
		return generateIdField;
	}

	/**
	 * Tells the codegen to generate ID Fields for the Individual name
	 * (rdf:about, rdf:id or the like) for all generated classes <br />
	 * Default value is <code>false</code>.
	 * 
	 * @param generateIdField
	 *            if ID fields should be generated
	 * @see #setIdFieldName(String)
	 */
	public void setGenerateIdField(boolean generateIdField) {
		this.generateIdField = generateIdField;
	}

	/**
	 * @return the name of the generated ID field
	 */
	public String getIdFieldName() {
		return idFieldName;
	}

	/**
	 * Sets the name of the owl Individual id field in the generated Java
	 * classes.<br/>
	 * Default value is <code>"name"</code>.
	 * 
	 * @param idFieldName
	 * @see #setGenerateIdField(boolean)
	 */
	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}

	/**
	 * @return if a ObjectFactory will be generated
	 */
	public boolean isCreateObjectFactory() {
		return createObjectFactory;
	}

	/**
	 * Tells the codegen whether an ObjectFactory shall be generated or not. The
	 * ObjectFactory will hold all the necessary methods to generate new
	 * Instances of the Class defined in the Ontology programmatically.<br/>
	 * Default value is <code>true</code>.
	 * 
	 * @param createObjectFactory
	 *            the createObjectFactory to set
	 * @see #setObjectFactoryName(String)
	 */
	public void setCreateObjectFactory(boolean createObjectFactory) {
		this.createObjectFactory = createObjectFactory;
	}

	/**
	 * @return the objectFactoryName
	 */
	public String getObjectFactoryName() {
		return objectFactoryName;
	}

	/**
	 * Sets the name of the ObjectFactory Java class.<br>
	 * Default value is <code>"ObjectFactory"</code>.
	 * 
	 * @param objectFactoryName
	 *            the objectFactoryName to set
	 * @see #setCreateObjectFactory(boolean)
	 */
	public void setObjectFactoryName(String objectFactoryName) {
		this.objectFactoryName = objectFactoryName;
	}

	public void setIgnoreProperties(List<String> ignoreProperties) {
		this.ignoreProperties = ignoreProperties;
	}
}
