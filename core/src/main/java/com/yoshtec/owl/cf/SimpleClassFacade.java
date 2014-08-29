package com.yoshtec.owl.cf;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoshtec.owl.Const;
import com.yoshtec.owl.PropertyType;
import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlIndividualId;
import com.yoshtec.owl.annotations.OwlObjectProperty;
import com.yoshtec.owl.annotations.OwlTransient;

class SimpleClassFacade<T> extends BaseCF<T> implements ClassFacade {
	
    /** Logger */
    static final Logger log = LoggerFactory.getLogger(SimpleClassFacade.class);
	
	/** ID access property */
	private PropertyAccessor idProp = null;
	
	/** ID Setting constructor */
	private Constructor<?> idConstructor = null;
	
	/** Property associated with the class */
	private Map<IRI, PropertyAccessor> properties = new HashMap<IRI, PropertyAccessor>();
	private Map<IRI, PropertyAccessor> dataProperties = new HashMap<IRI, PropertyAccessor>();
	private Map<IRI, PropertyAccessor> objectProperties = new HashMap<IRI, PropertyAccessor>();
	
	
	protected SimpleClassFacade(Class<T> clazz, XsdTypeMapper typeMapper){
		super(clazz, typeMapper);
		this.clazz = clazz;
		setRepresentedClass();
	}
	
	private void setRepresentedClass() throws IllegalStateException {
		
		this.readClasses();
		
		this.readPackage();
		
		this.readIdConstructor();
		
		log.trace("FIELDS");
		// walk through all superclasses
		for( Class<?> cl : getSuperClasses(clazz)){
			
		    // and collect the declared fields of owlclasse
		    if( !( cl.isAnnotationPresent(OwlClass.class) ||
		           cl.isAnnotationPresent(OwlClassImplementation.class)) ){
		        continue; // spare non owl classes!
		    }
		    
		    for( Field field : cl.getDeclaredFields() ){
				log.trace("   F: {} ", field.toGenericString());
				
				// only access if not transient, OwlTransient or static
				if(!(	Modifier.isTransient(field.getModifiers()) ||
						field.isAnnotationPresent(OwlTransient.class) ||
						Modifier.isStatic(field.getModifiers()) )){
				
				    addProperty(field);
					
					// this property will give an individual id
					if(field.isAnnotationPresent(OwlIndividualId.class)){
						this.idProp = new FieldPropertyAccessor(field, null, typeMapper);
					}
					
					// TODO: Implement Convention before Configuration 
					// use conventions on the fields to marshall unmarshall them
				}
			}
		}

		log.trace("METHODS:");
		for( Method method : clazz.getMethods()) {
			log.trace("   M: {}", method.toGenericString());
			
			// TODO find the correct assessor method pairs
			if( method.isAnnotationPresent(OwlDataProperty.class) ){
			    
			} else if ( method.isAnnotationPresent(OwlObjectProperty.class) ){
			    
			}
			
			// this property will give an individual id
			if(method.isAnnotationPresent(OwlIndividualId.class)){
				this.idProp = new PropPropertyAccessor(method, null, null, typeMapper);
			}
		}
		
		
		
		//check for some common Problems:
		if( classuris.isEmpty() ){
			String msg = "This class is not a correctly annotated Class " + clazz.getCanonicalName();
			log.warn(msg);
			throw new IllegalStateException(msg);
		}
		
		if( this.ontoBaseUri == null ){
			log.warn("No ontology base URI via package-info.java was specified!");
			// use the default part of the uri specified via the OwlClass
			IRI uri = classuris.iterator().next();
			String newUri = uri.toString().replaceAll(uri.getFragment(), "").replaceAll("#", "");
			ontoBaseUri = IRI.create(newUri);
			
			log.warn("Defaulting to: {}", newUri);
			
		}
		
	}
	
	private void readIdConstructor(){
	    try {
	        Constructor<?> con = clazz.getConstructor(String.class);
	        log.debug(" CO: {}", con.toGenericString());
	        if(con.isAnnotationPresent(OwlIndividualId.class)){
	            idConstructor = con;
	        }
	    } catch (NoSuchMethodException e) {
	        log.debug("Could not find id Constructor. ");
	    }
	}
	
	/**
	 * Checks if the passed class is declared as an OwlClass and
	 * tries to retrieve the OwlClass URI.
	 * @param clazz to check for an OwlClass URI
	 * @return {@code true} if an valid URI was found and added to URI list
	 */
	private boolean addClassUri(Class<?> clazz){
	    if( clazz.isAnnotationPresent(OwlClass.class) ){
            OwlClass oc = clazz.getAnnotation(OwlClass.class);
            try{
                final IRI uri;
                // If it is default string then use the simple class name 
                if(oc.uri().equals(Const.DEFAULT_ANNOTATION_STRING)){
                    uri = IRI.create('#' + clazz.getSimpleName());
                } else {
                    uri = IRI.create(oc.uri()); 
                }
                // TODO: Handle partial Uris e.g. '#Foo' 
                log.debug("Found Class '{}' with URI: '{}'", clazz.getName(), uri);
                classuris.add(uri);
                return true;
            } catch (IllegalArgumentException ie){
                log.warn("Unable to create URI from class: '{}'", clazz);
                return false;
            }
        }
	    return false;
	}
	
	/**
	 * 
	 */
	private void readClasses(){
		boolean indHasOwlclass = false;
		
		log.debug("CLASS");
		log.debug(" C: {}", clazz);
		// add this Class or interface to the Individual
		// if unable to find the uri at the class
		if(!addClassUri(clazz)){
		    //continue search for it at the interfaces 
		    
		    log.debug("INTERFACES:");
		    for(Class<?> iface : clazz.getInterfaces()){
		        log.debug(" I: {}", iface.getName());
		        indHasOwlclass |= addClassUri(iface);
		    }
		    
		    if(!indHasOwlclass){
		        // still not found, search even further
		        log.debug("SUPERCLASSES:");
		        for(final Class<?> u : getSuperClasses(clazz)){
		            log.debug(" S: {}", u.getName());
		            // look only for further classes in the normal java class hierarchy if 
		            // there is not already one associated with this individual
		            if( u.isAnnotationPresent(OwlClass.class) && !indHasOwlclass ){
		                indHasOwlclass |= addClassUri(u);
		            }
		        }
		    }
		}
	}
	
	private void addProperty(Field field){
        
	    if( field.isAnnotationPresent(OwlDataProperty.class) ){
            OwlDataProperty odp = field.getAnnotation(OwlDataProperty.class);
            addProperty(field, odp.uri(), PropertyType.DATA);
        } else if( field.isAnnotationPresent(OwlObjectProperty.class) ){
            OwlObjectProperty oop = field.getAnnotation(OwlObjectProperty.class);
            addProperty(field, oop.uri(), PropertyType.OBJECT);
        } else {
            PropertyType ptype = PropertyType.OBJECT;
            // no annotation present, lets have a look on the type 
            Class<?> cl = field.getType();
            if( cl.isArray() ){
                // TODO: Detection of the type
                throw new IllegalStateException("Arrays currently not supported in automatic Type detection");
            } else if( Collection.class.isAssignableFrom(cl) ){
                // TODO: Accurate detection of the type 
                log.debug("XXXX");
            } 
            
            if( typeMapper.getXsdType(cl) != null ){
                ptype = PropertyType.ID;
            }
            log.debug("Suspecting {} to be a {} - Property", field.toGenericString(), ptype);
            
            addProperty(field, Const.DEFAULT_ANNOTATION_STRING, ptype);
        }
	}
	
	private void addProperty(Field field, String uri, PropertyType type){
		final IRI propUri; 
		if(uri.equals(Const.DEFAULT_ANNOTATION_STRING)){
		    propUri = IRI.create("#" + field.getName());
		} else {
		    propUri = IRI.create(uri);
		}
		// Only add the Property if it is not already defined
		// e.g. it may be overwritten in an subclass
		if(!properties.containsKey(propUri)){
			PropertyAccessor prop = new FieldPropertyAccessor(field, propUri, typeMapper);
			properties.put(propUri, prop );
			switch (type) {
			case DATA:
				dataProperties.put(propUri, prop);
				break;
			case OBJECT:
				objectProperties.put(propUri, prop);
				break;
			case ID:
				log.warn("Unable to handle ID Properties at this level");
			default:
				break;
			}
		}
	}
	
	
	/**
	 * @return SuperClasses of {@code c} including {@code c} 
	 * itself, excluding {@code java.lang.Object}. If {@code c}
	 * is {@code null} then empty.
	 */
	private Collection<Class<?>> getSuperClasses(Class<?> c){
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		Class<?> u = (c != null ? c : Object.class);
		// Object is not interesting
		while(!u.equals(Object.class)){
			//include this class
			result.add(u);
			u = u.getSuperclass();
		}
		return result;
	}

	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#getProperty(org.semanticweb.owlapi.model.IRI)
     */
	public PropertyAccessor getProperty(IRI uri){
		return this.properties.get(uri);
	}
	
	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#hasProperty(org.semanticweb.owlapi.model.IRI)
     */
	public boolean hasProperty(IRI uri){
		return this.properties.containsKey(uri);
	}
	

	
	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#getIdString(java.lang.Object)
     */
	public String getIdString(Object o){
		if( idProp != null){
			try {
				Object value = idProp.getValue(o);
				if(value != null){
					return value.toString();
				}
			} catch (Exception e) {
				log.warn("could not retrieve idValue for object {}",o);
			}
			
		}
		
		// defaulting to the hashValue
		return Integer.toString(o.hashCode());
		
	}
	

	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#getDataProperties()
     */
	public Collection<PropertyAccessor> getDataProperties(){
		return dataProperties.values();
	}
	
	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#getObjectProperties()
     */
	public Collection<PropertyAccessor> getObjectProperties(){
		return objectProperties.values();
	}
	
	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#getNewInstance()
     */
	public Object getNewInstance() throws Exception{
		return clazz.newInstance();
	}

	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#hasSetableId()
     */
	public boolean hasSetableId() {
		return idProp != null && idProp.isAccessible();
	}

	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#getNewInstance(java.lang.String)
     */
	public Object getNewInstance(String id) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if(idConstructor != null){
			return idConstructor.newInstance(id);
		}
//		if( idCreator != null ){
//		    return idCreator.invoke(null, args);
//		}
		return clazz.newInstance();
	}
	
	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#setId(java.lang.Object, java.lang.String)
     */
	public void setId(Object obj, String id) {
		if( idProp != null )
			idProp.setOrAddValue(obj, id);
	}

	/* (non-Javadoc)
     * @see com.yoshtec.owl.cf.ClassFacade#commit()
     */
	public void commit() throws InvocationTargetException, IllegalAccessException{
		if(idProp != null){
			idProp.commit();
		}
		
		for(PropertyAccessor prop : properties.values()){
			prop.commit();
		}
	}


}
