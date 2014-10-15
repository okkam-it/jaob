package com.yoshtec.owl.jcodegen;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.yoshtec.owl.PropertyAccessType;
import com.yoshtec.owl.PropertyType;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlDataTypes;
import com.yoshtec.owl.annotations.OwlIndividualId;
import com.yoshtec.owl.annotations.OwlObjectProperty;
import com.yoshtec.owl.annotations.dprop.OwlFunctionalDataProperty;
import com.yoshtec.owl.annotations.oprop.OwlFunctionalObjectProperty;

/**
 * Abstraction for OWL Properties. Bridges the OWL Property and Java
 * Field setter/getter world.
 * 
 * @author Jonas von Malottki
 *
 */
/* package */ class Property {

	private static final Logger log = LoggerFactory.getLogger(Property.class);
	
	/** Name of the Property */
	private String name = null;
	
	/** URI of the Property */
	private URI propUri = null;
	
	/** Functional Property? e.g. can only be present zero or one times */
	private boolean functional = false;
	
	/** The Java type representing this Property */
	private JType type = null;
	private JInterfaceProxy jproxy = null;
	
	/** List of DataType URIs */
	private List<URI> dtUris = new ArrayList<URI>();
	
	//TODO: support more access types like Jaxb
	/** Access type, see: {@link PropertyAccessType} */
	private PropertyAccessType assess = PropertyAccessType.FIELD;
	
	/** the type of the Property, see {@link PropertyType} */
	private PropertyType ptype = null;
	
	/** List of {@code rdfs:comment}s as String */
	private List<String> comments = new ArrayList<String>();
	
	public Property(){
	}
	
	public Property(String name, URI uri, boolean functional, JType type){
		this.propUri = uri;
		this.name = name;
		this.functional = functional;
		this.type = type;
	}
	
	/**
	 * @return variable name in Java Style
	 */
	public String getVArname(){
		String vArname = null;
		try{
			vArname = name.substring(0,1).toUpperCase() + name.substring(1);
		} catch (Exception e){
			// this only happens if there is only one letter.
			vArname = name.toUpperCase();
		}
		return vArname;
	}
	
	public void addIdConstructor(JDefinedClass jclass){
		if( !jclass.isInterface() ){	
			// add default constructor:
			jclass.constructor(JMod.PUBLIC);
			
			//and build an Id constructor
			JMethod constructor = jclass.constructor(JMod.PUBLIC);
			JVar cvar = constructor.param(String.class, this.name);
			constructor.annotate(OwlIndividualId.class);
			constructor.javadoc().add("Convenience constructor for setting the OWL ID");
			// Output: this.field = field	
			constructor.body().assign(JExpr._this().ref(cvar), cvar);
		}
	}
	
	/**
	 * Adds an Methods, Fields and the Implementation to the JClass if it is
	 * a real class, just puts Method stubs if it is an interface. 
	 * @param jclass the JClass to add the implementation to 
	 * @param inheritDoc if the documentation should be inherited
	 */
	public void addImplementationTo(JDefinedClass jclass, boolean inheritDoc){

		// Prepare some variables
		JClass type = this.getJavaType();
		String varName = this.getVArname();
		String jdoc = this.getJavaDoc();
		
		// Assemble the Implementation
		
		// Methods
		JMethod getter = jclass.method(JMod.PUBLIC, type, "get" + varName);
		JMethod setter = jclass.method(JMod.PUBLIC, void.class, "set" + varName );
		JVar setvar = setter.param(type, this.name);

		/// Building the Implementation Field
		if(!jclass.isInterface()){
			JFieldVar field = jclass.field(JMod.PROTECTED, type, this.name);

			field.javadoc().add(jdoc);

			switch (this.ptype) {
			case DATA:
				field.annotate(OwlDataProperty.class).param("uri", propUri.toString());
				if(this.functional){
					field.annotate(OwlFunctionalDataProperty.class);
				}
				break;
			case OBJECT:
				field.annotate(OwlObjectProperty.class).param("uri", propUri.toString());
				if(this.functional){
					field.annotate(OwlFunctionalObjectProperty.class);
				}
				break;
			case ID:
				field.annotate(OwlIndividualId.class);
				
				this.addIdConstructor(jclass);
				
				break;
			default:
				log.warn("Unknown Property Type: {}",ptype);
				break;
			}

			// annotate field
			// Data type URIS
			if(!dtUris.isEmpty()){
				if(dtUris.size() == 1){
					field.annotate(OwlDataType.class).param("uri", dtUris.get(0).toString());
				} else {
					JAnnotationArrayMember annot = field.annotate(OwlDataTypes.class).paramArray("value");
					for(URI uri : dtUris){
						annot.annotate(OwlDataType.class).param("uri", uri.toString());
					}
				}
			}

			JBlock gbody = getter.body();
			if(!functional){
				JClass al = jclass.owner().ref(ArrayList.class).narrow(this.getBaseType().boxify());
				//Output: if(field == null) field = new ArrayList<Type>(); 
				gbody._if(field.eq(JExpr._null()))._then().assign(field, JExpr._new(al));
			}
			gbody._return(field);

			// Setter Body
			// Output: this.field = field	
			setter.body().assign(JExpr._this().ref(field), setvar);
		} 

		if(inheritDoc){
			// inherit documentation
			setter.javadoc().add("{@inheritDoc}");
			getter.javadoc().add("{@inheritDoc}");
		} else {
			// make fresh jdoc 
			
			JDocComment	getterdoc = getter.javadoc();
			JDocComment setterdoc = setter.javadoc();

			setterdoc.add(jdoc);
			getterdoc.add(jdoc);

			if(this.functional){
				getterdoc.addReturn().add("the ".concat(this.name));
				setterdoc.addParam(setvar).add("the new ".concat(name).concat(" value"));
			} else {
				getterdoc.addReturn().add("a List of ".concat(this.name));
			}
		}
	}
	
	/**
	 * @return JavaDoc string of this Property
	 */
	public String getJavaDoc(){
		StringBuffer sb = new StringBuffer();
		switch (ptype) {
		case DATA:
			sb.append("OWL Data Property:</br>\n");
			break;
		case OBJECT:
			sb.append("OWL Object Property:</br>\n");
			break;
		case ID: 
			// ID property 
			sb.append("rdf:about or rdf:id, used to generate the individual URI");
			// no further annotations to make!
			return sb.toString();
		default:
			// nop
			break;
		}
		
		sb.append("<code>");
		sb.append(this.propUri.toString());
		sb.append("</code>");
		for (String comment : comments) {
			sb.append("</br>\nOWLComment: ");
			sb.append(comment);
		}
		return sb.toString();
	}

	
	/**
	 * @return the boxed type if this Property is functional, 
	 * a narrowed {@code List<type>} of the type else 
	 */
	private JClass getJavaType(){
		JClass type = null;
		if(this.type == null){
			type = this.jproxy.getType();
		} else {
			type = this.type.boxify();
		}
		if(!this.functional){
			type = type.owner().ref(List.class).narrow(type);
		}
		return type;
	}
	
	@Override
	// override for HashSet usage
	public boolean equals(Object obj) {
		if( obj instanceof Property)
			return ((Property)obj).name.equals(this.name);
		return false;
	}
	
	@Override
	// override for HashSet usage.
	// package local class! Not for usage outside of package
	public int hashCode() {
		return name.hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URI getPropUri() {
		return propUri;
	}

	public void setPropUri(URI propUri) {
		this.propUri = propUri;
	}

	public boolean isFunctional() {
		return functional;
	}

	public void setFunctional(boolean functional) {
		this.functional = functional;
	}

	public JType getBaseType() {
		if(type == null){
			return jproxy.getType();
		}
		return type;
	}

	public void setBaseType(JType type) {
		this.jproxy = null;
		this.type = type;
	}
	
	public void setBaseType(JInterfaceProxy proxy){
		this.type = null;
		this.jproxy = proxy;
	}
	

	public PropertyAccessType getAssess() {
		return assess;
	}

	public void setAssess(PropertyAccessType assess) {
		this.assess = assess;
	}

	public PropertyType getPtype() {
		return ptype;
	}

	public void setPtype(PropertyType ptype) {
		this.ptype = ptype;
	}

	public List<URI> getDtUris() {
		return dtUris;
	}
	
	public void addDatatype(URI uri){
		dtUris.add(uri);
	}

	/**
	 * adds a comment to this property
	 */
	public void addComment(String comment){
		this.comments.add(comment);
	}
	
	/**
	 * @return the comments
	 */
	public List<String> getComments() {
		return comments;
	}
}
