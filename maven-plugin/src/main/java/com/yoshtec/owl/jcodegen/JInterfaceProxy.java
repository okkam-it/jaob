package com.yoshtec.owl.jcodegen;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.yoshtec.owl.Const;
import com.yoshtec.owl.PropertyType;
import com.yoshtec.owl.annotations.OwlAnnotation;
import com.yoshtec.owl.annotations.OwlAnnotations;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.classes.OwlObjectIntersectionOf;
import com.yoshtec.owl.annotations.classes.OwlObjectUnionOf;
import com.yoshtec.owl.annotations.classes.OwlSubclassOf;
import com.yoshtec.owl.annotations.classes.OwlThing;
import com.yoshtec.owl.util.OntologyUtil;

/* package */ class JInterfaceProxy {
	
    /** Logger */
	static private final Logger log = LoggerFactory.getLogger(JInterfaceProxy.class);
	
	/** 
	 * Local class for Owl Annotation Mapping in Hashsets. 
	 *
	 */
	private class OAnnot{
		public String uri = "";
		public String content = "";
		public String dturi = "";
		public boolean comment = false;
		
		public OAnnot(String uri, String content, String dturi, boolean comment) {
			super();
			this.uri = uri;
			this.content = content;
			this.dturi = dturi;
			this.comment = comment;
		}

		/* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result
                    + ((content == null) ? 0 : content.hashCode());
            result = prime * result + ((uri == null) ? 0 : uri.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            OAnnot other = (OAnnot) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (content == null) {
                if (other.content != null)
                    return false;
            } else if (!content.equals(other.content))
                return false;
            if (uri == null) {
                if (other.uri != null)
                    return false;
            } else if (!uri.equals(other.uri))
                return false;
            return true;
        }

        private JInterfaceProxy getOuterType() {
            return JInterfaceProxy.this;
        }
	}
	
	/** The interface Represented */
//	private JDefinedClass iface = null;
	
	/** the implementation if there is one */
//	private JDefinedClass impl = null;
	
	/** the Suffix appended to the Implementation */
	private String classSuffix = "";
	
	/** Super classes of this interface / class */
	private Set<JInterfaceProxy> directSuperClasses = new HashSet<JInterfaceProxy>();
	
	/** Other interfaces implementing this one */
	private Set<JInterfaceProxy> implementors = new HashSet<JInterfaceProxy>();
	
	/** Properties Associated with this Interface */
	private Set<Property> properties = new HashSet<Property>();
	
	/** local properties declared for this one only */
	private Set<Property> localProperties = new HashSet<Property>();
	
	/** the Name of the Interface / Class to be generated */
	private String name = null;
	
	/** the Class URI of this OWL class */
	private URI classUri = null;
	
	/** Annotations read from the ontology */
	private Set<OAnnot> annotations = new HashSet<OAnnot>();
	
	/** whether to create interfaces or not */
	private boolean createInterface = false;
	
	/** Package where the Classes / interfaces will be created */
	private JPackage jpack = null;
	
	/** the Name/id Property of this class if it should be generated */ 
	private Property idProperty = null;
	
	private Set<JInterfaceProxy> unionOf = new HashSet<JInterfaceProxy>();
	
	private Set<JInterfaceProxy> intersectionOf = new HashSet<JInterfaceProxy>();
	
	/**
	 * Creates a new Proxy
	 * @param name Name of the Class / Interface
	 * @param jpack Package where the class / interface will be created
	 */
	public JInterfaceProxy(String name, JPackage jpack, boolean createInterface, String classSuffix){
		this.name = name;
		this.jpack = jpack;
		this.createInterface = createInterface;
		this.classSuffix = classSuffix;
	}
	
	/**
	 * Add a new Property to this Class interface
	 * and set the inheritance
	 * 
	 * @param prop Property to be added
	 */
	public void addProperty(Property prop){
		// it is declared for this one
		this.localProperties.add(prop);

		// and also for all subclasses / interfaces
		this.addPropX(prop);

	}
	
	/**
	 * Adds the property and delegates it to the 
	 * implementors of this class / interface
	 * @param prop
	 */
	private void addPropX(Property prop){
		if(prop.getPtype() == PropertyType.ID){
			this.idProperty = prop;
		}
		
		// Propagate to the last interface
		for(JInterfaceProxy iface : implementors){
			iface.addPropX(prop);
		}
		
		// let this one have it
		this.properties.add(prop);
	}
	
	public void _implements(JInterfaceProxy iface){
		
		this.directSuperClasses.add(iface);
		
		// implements
		//this.iface._implements(iface.iface);
		// add this one to the other one
		iface.implementors.add(this);

	}

	/**
	 * @return a newly created Interface with this name or the reference to 
	 * an already existing one.
	 */
	protected JDefinedClass getInterface() {
		JDefinedClass iface = jpack._getClass(name);
		if(iface == null){
			try {
				iface = jpack._interface(name);
			} catch (JClassAlreadyExistsException e) {
				log.debug("Already existent Interface: " + name ,e);
			}
		}
		return iface;
	}
	/**
	 * 
	 * @param suffix
	 * @return the reference to the existent Implementation or a newly created one
	 */
	protected JDefinedClass getImplementation() {
		JDefinedClass jclass = jpack._getClass(name.concat(classSuffix));
		if(jclass == null){
			try {
				jclass = jpack._class(name.concat(classSuffix));
			} catch (JClassAlreadyExistsException e) {
				log.debug("Already existent Class: " + name ,e);
			}
		}
		return jclass;
	}

	// Proxy Methods
	public String fullName() {
		return jpack.name().concat(".").concat(name);
	}

	public String name() {
		return name;
	}
	
	
	public void addImplementation(String javaClassSuffix){
		
		JDefinedClass impl = this.getImplementation();
		
		if( this.createInterface ){
			JDefinedClass iface = this.getInterface();
			
			// subclassing of interfaces
			for( JInterfaceProxy superclass : directSuperClasses ){
				iface._implements(superclass.getInterface());
			}
			
			if( !intersectionOf.isEmpty() ){
			    
			    JAnnotationArrayMember annot = iface.annotate(OwlObjectIntersectionOf.class).paramArray("classes");
			    
			    for( JInterfaceProxy intersectionclass : intersectionOf ){
			        iface._implements(intersectionclass.getInterface());
			        annot.param(intersectionclass.getClassUri().toString());
			    }
			    
			}
			
			if( !unionOf.isEmpty() ){
			    JAnnotationArrayMember annot = iface.annotate(OwlObjectUnionOf.class).paramArray("classes");
			    for( JInterfaceProxy prx : unionOf ){
			        annot.param(prx.getClassUri().toString());
			        prx.getInterface()._implements(this.getInterface());
			    }
			    
			}
			
			// create interface Properties
			for( Property prop : localProperties ){
				prop.addImplementationTo(iface, false);
			}
			
			// create implementation for Properties
			for( Property prop : properties ){
				prop.addImplementationTo(impl, true);
			}
		
			// add annotations to the Interface
			this.annotateJClass(iface);
			
			// make it an implementation 
			impl._implements(iface);
			
			// annotate the implementation with a pointer to the owlclass
		    JAnnotationUse jannot = impl.annotate(OwlClassImplementation.class);
		    JAnnotationArrayMember annotmember = jannot.paramArray("value");
		    annotmember.param(iface);
			
		} else {
			
			// Subclassing of the implementation classes
			for( JInterfaceProxy superclass : directSuperClasses ){
				impl._extends(superclass.getImplementation());
			}
			
			// create implementation Properties
			for( Property prop : localProperties ){
				prop.addImplementationTo(impl, false);
			}
			
			// add annotations to the Implementation
			this.annotateJClass(impl);
			
		}

		// adding Id constructor if id prop is present
//		if(idProperty != null){
//			idProperty.addIdConstructor(impl);
//		}
	}

	private void annotateJClass(JDefinedClass jclass){

		if( !classUri.equals(OntologyUtil.OWL_THING_URI) ){
			//Annotate Owlclass with its URI
			jclass.annotate(OwlClass.class).param("uri", classUri.toString());
		} else {
			// annotate special meaning for owl:thing
			jclass.annotate(OwlThing.class);
		}
		
		// add some informative Javadoc
		jclass.javadoc().append("Generated Class from Ontology:\n");
		jclass.javadoc().append("Class URI: <code>"+ this.classUri.toString() + "</code>\n");
		
		// superclasses 
		if( !directSuperClasses.isEmpty() ){
			JAnnotationArrayMember annot = jclass.annotate(OwlSubclassOf.class).paramArray("value");
			for( JInterfaceProxy jiface : directSuperClasses ){
				annot.annotate(OwlClass.class).param("uri", jiface.classUri.toString());
			}
		}
		
		if( !annotations.isEmpty() ){
			JAnnotationArrayMember oannotsarray = jclass.annotate(OwlAnnotations.class).paramArray("value");
		
			for( OAnnot annot : annotations ){
				JAnnotationUse jannot = oannotsarray.annotate(OwlAnnotation.class);
				jannot.param("uri", annot.uri);
				jannot.param("content", annot.content);
				jannot.param("dataTypeUri", annot.dturi);
				
				// Comments can go to the Javadoc as well
				if(annot.comment){
					// add value to Javadoc 
					jclass.javadoc().append("\n<br>\nOWLComment: ");
					// TODO: break too long lines..
					jclass.javadoc().append(annot.content);
				}
			}
		}
		
	}
	
	/**
	 * Adds the creation code to the passed {@link JDefinedClass}
	 * @param factory to add the factory Method to
	 */
	public void addtoObjectFactory(JDefinedClass factory){
	
		JDefinedClass returntype = (createInterface ? getInterface() : getImplementation());
		
		JMethod creator = factory.method(JMod.PUBLIC, returntype, Const.CREATE_PREFIX + name);

		// result: return new ClassName
		JDefinedClass impl = this.getImplementation();
		creator.body()._return(JExpr._new(impl));
		
		creator.javadoc().add("Create an instance of {@link " + returntype.name() + "}");
		
		// create an id factory method
		if( this.idProperty != null ){
			JMethod idcreator = factory.method(JMod.PUBLIC, returntype, Const.CREATE_PREFIX + name);
			idcreator.param(String.class, idProperty.getName());
			JInvocation newidclass = JExpr._new(impl);
			newidclass=newidclass.arg(JExpr.ref(idProperty.getName()));
			idcreator.body()._return(newidclass);
			
			creator.javadoc().add("Create an instance of {@link " + returntype.name() + "}");
			creator.javadoc().addParam(idProperty.getName()).add("the name of this Object");
		}

	}
	
	/**
	 * Adds an annotation to the Class / Interface
	 * @param uri
	 * @param content
	 * @param dturi
	 * @param comment
	 */
	public void addAnnotation(URI uri, String content, String dturi, boolean comment){
		OAnnot annot = new OAnnot(uri.toString(), content, dturi, comment);
		this.annotations.add(annot);
	}
	
	/**
	 * @return the Class URI 
	 */
	public URI getClassUri() {
		return classUri;
	}

	/**
	 * Sets the class Uri of the Owl class
	 * @param classUri
	 */
	public void setClassUri(URI classUri) {
		this.classUri = classUri;
	}
	
	/**
	 * @return if this JInterfaceProxy can be a class on its own. 
	 * This is the case if this class has none or only one superclass
	 * otherwise it has to be an interface.
	 */
	public boolean isClassable(){
		return directSuperClasses.isEmpty() || directSuperClasses.size() == 1;
	}

	public JClass getType() {
		if(createInterface){
			return this.getInterface();
		}
		return this.getImplementation();
	}

	/**
	 * 
	 * @return whether an interface will be generated {@code true} or not
	 */
	public boolean isCreateInterface() {
		return createInterface;
	}

	/**
	 * @param createInterface whether an interface shall
	 *  be generated {@code true} or not {@code false}
	 */
	public void setCreateInterface(boolean createInterface) {
		this.createInterface = createInterface;
	}
	
	/**
	 * If this class creates a Interface implementation pair,
	 * how the class should be suffixed:
	 * <br>
	 * Example:
	 * OwlClass Name: Apple
	 * Suffix: Impl
	 * 
	 * Java Interface Name: Apple
	 * Java Class Name: AppleImpl
	 * 
	 * @param s the Suffix
	 */
	public void setImplementationSuffix(String s){
		this.classSuffix = s;
	}
	
	
	public String getImplementationSuffix(){
		return this.classSuffix;
	}
	
	public void addIntersection(JInterfaceProxy prx){
	    if( prx != null ){
	        intersectionOf.add(prx);
	        this._implements(prx);
	    }
	}
	
	public void addUnion(JInterfaceProxy prx){
	    if( prx != null ){
	        unionOf.add(prx);
	        prx._implements(this);
	    }
	}
	
}
