package com.yoshtec.owl.cf;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoshtec.owl.Const;
import com.yoshtec.owl.PropertyAccessType;
import com.yoshtec.owl.XsdType;
import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlDataTypes;
import com.yoshtec.owl.annotations.dprop.OwlFunctionalDataProperty;
import com.yoshtec.owl.annotations.oprop.OwlFunctionalObjectProperty;
/**
 * This Class helps to access annotated Properties of 
 * Java Beans and link the concepts to the OWL world.
 * 
 * @author Jonas von Malottki
 *
 */
public class FieldPropertyAccessor implements PropertyAccessor{

    private final static Logger log = LoggerFactory.getLogger(PropertyAccessor.class); 

    private XsdTypeMapper typeMapper = null;
    
    // array or collection value?
    private boolean collection = false;
    private boolean array = false;
    
    private boolean functional = false;
    
    private Field field = null;

    /** List of data types */
    private Set<IRI> dtUri = new HashSet<IRI>();
    private IRI propUri = null;
    private Class<?> declaringClass = null;
    
    /** allows to do lazy property setting, it is mostly used to be able to cache
     * Collections or arrays that would be expensive to copy */
    protected Map<Object, Object> valueCache;
    
    /** will be used to marshal an unmarshall literals */
    private XmlAdapter<String, Object> adapter;
    

    public FieldPropertyAccessor(Field field, IRI propUri, XsdTypeMapper typeMapper) {
        this.init(field, propUri, null, typeMapper);
    }

    public FieldPropertyAccessor(Field field, IRI propUri, XmlAdapter<?, ?> adapter, XsdTypeMapper typeMapper){
        this.init(field, propUri, adapter, typeMapper);
    }
    
    private void init(Field field, IRI propUri, XmlAdapter<?, ?> adapter, XsdTypeMapper typeMapper){
        this.typeMapper = typeMapper;
        this.field = field;
        this.field.setAccessible(true);
        this.collection = Collection.class.isAssignableFrom(field.getType()); 
        this.array = field.getType().isArray();
        this.declaringClass = field.getDeclaringClass();
        this.propUri = propUri;
        extractDataTypes();
        
        this.functional = field.isAnnotationPresent(OwlFunctionalDataProperty.class) 
                || field.isAnnotationPresent(OwlFunctionalObjectProperty.class);
    }

    private void extractDataTypes(){
        if(!extractDataTypes(field)){
            dtUri.add(reflectOwlType(field.getGenericType()));
        }
    }
    
    private IRI reflectOwlType(Type type){
        Type actualType = type;
        
        if( array ){
            Class<?> at = ((Class<?>)type).getComponentType();
            if(!at.isArray()){
                actualType = at;
            } else {
                log.error("Unable to handle nested Arrays: {}",type);
                return null;
            }
        }
        
        if( collection ){
            if(type instanceof ParameterizedType){
                ParameterizedType pt = (ParameterizedType)type;
                Type[] typeparams = pt.getActualTypeArguments();
                if( !(typeparams.length > 1 || typeparams.length == 0) ){
                    actualType = typeparams[0];
                }
            }
        }
        
        log.debug("Actual Type {} of former Type {}", actualType, type);
        if(actualType instanceof Class<?>){
            Class<?> at = (Class<?>)actualType;
            if(at.isAnnotationPresent(OwlClass.class)){
                OwlClass oc = at.getAnnotation(OwlClass.class);
                final IRI returi;
                if(oc.uri().equals(Const.DEFAULT_ANNOTATION_STRING)){
                    log.warn("Default uristr on class", at);
                    returi = IRI.create("#" + at.getSimpleName());
                } else {
                    returi = IRI.create(oc.uri());
                }
                log.debug("Found URI '{}' for actual Type '{}'", returi, actualType );
                return returi;
            }
        } 
        // otherwise it will be mapped via standard way
        XsdType xsdtype = typeMapper.getXsdType(actualType); 
        
        log.debug("Found XSDType '{}' for actual Type '{}'", xsdtype, actualType );
        return xsdtype != null ? xsdtype.getUri() : null;
        
        
    }

    /**
     * Extracts Datatype Annotations from from an {@link AnnotatedElement}
     * and adds them to the dtUri variable.
     * @param ae 
     * @return true if annotations where found
     */
    private boolean extractDataTypes(AnnotatedElement ae){
        if( ae == null ){
            return false;
        }
        
        if(ae.isAnnotationPresent(OwlDataTypes.class)){
            // found collection of data types
            OwlDataTypes odts = ae.getAnnotation(OwlDataTypes.class);
            for(OwlDataType odt : odts.value()){
                dtUri.add(IRI.create(odt.uri()));
            }
            return true;
        } else if(ae.isAnnotationPresent(OwlDataType.class)){
            // only one data type present
            OwlDataType odt = ae.getAnnotation(OwlDataType.class);
            dtUri.add(IRI.create(odt.uri()));
            return true;
        }
        return false;
    }

    private void determineAdapter(AnnotatedElement ae){
        if(ae.isAnnotationPresent(XmlJavaTypeAdapter.class)){
            XmlJavaTypeAdapter xj = ae.getAnnotation(XmlJavaTypeAdapter.class);
            try {
                this.adapter = xj.value().newInstance();
            } catch (Exception e) {
                log.warn("Unable to create Adapter: "+ xj.value().toString(), e);
            } 
        }
    }
    
    /**
     * 
     * @return {@code false} if this Property is an Array or a Collection   
     */
    public boolean isSingleValue(){
        return !collection && !array;
    }

    /**
     * Sets the value or in Case of Collections will add the Value to the
     * Collection of the Object passed.
     * 
     * @param obj
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void setOrAddValue(Object obj, Object value) {

        if(valueCache == null){
             valueCache = new HashMap<Object, Object>();
        }
        
        //TODO -----------------------------
        if(this.isSingleValue()){
            valueCache.put(obj, value );
        } else {
            Collection<Object> c = (Collection<Object>)valueCache.get(obj);
            if(c == null){
                if(collection){
                    
                    // examine type of collection ?
                    Class<?> type = null;
                        type = field.getType();
                    
                    if( type == null ){
                       type = ArrayList.class; // use array list as a fall back
                    }
                
                    // create a new collection based on the type (Set or List)
                    if( List.class.isAssignableFrom(type) ){
                        c = new ArrayList<Object>();
                    } else if (Set.class.isAssignableFrom(type)){
                        c = new HashSet<Object>();
                    } else {
                        log.warn("unknown Collection {} trying default", type);
                        c = new ArrayList<Object>();
                    }
                } else {
                    c = new ArrayList<Object>();
                }
                valueCache.put(obj, c);
            } 
            c.add(value);
        }
    }
    /**
     * Commits the values from the cache to the Objects 
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void commit() throws IllegalAccessException, InvocationTargetException {

        if( valueCache == null )
            return; // nothing to to do in this case
        
        if( field == null ) {
            throw new IllegalStateException("No way to set the value");
        }
        
        for(Object obj : valueCache.keySet()){
            Object value = valueCache.get(obj);
            if(!array){
                field.set(obj, value);
            } else {
                field.set(obj, ((Collection<?>)value).toArray());
            }
//            break;
        }
        // empty the value cache
        valueCache = null;
    }
    

    /**
     * Retrieves a Value from a Property. Primitives will be boxed.
     * @param obj the Object the Value should be retrieved from
     * @return the value of the Property from the Object
     * @throws Exception
     */
    public Object getValue(Object obj) throws IllegalAccessException, InvocationTargetException{
        return field.get(obj);
    }
    
    public Collection<String> getLiterals(Object obj) throws Exception {
        Object value = this.getValue(obj);
        
        ArrayList<String> result = new ArrayList<String>();
        
        if( value instanceof Collection<?> ){
            for( Object lv : ((Collection<?>)value) ){
                 result.add(adapter.marshal(lv));
            }
        } else if( value instanceof Object[]){ // or Object Arrays
            for( Object lv : ((Object[])value) ){
                result.add(adapter.marshal(lv));
            } 
        } else { //seems to be a single Value
            result.add(adapter.marshal(value));
        }
        return result;
    }
    
    /**
     * @return the propUri
     */
    public IRI getPropUri() {
        return propUri;
    }

    /**
     * @param propUri the propUri to set
     */
    public void setPropUri(IRI propUri) {
        this.propUri = propUri;
    }
    
    /**
     * @return the Set of Datatype URIs valid for this property
     */
    public Set<IRI> getDataTypeUris(){
        return this.dtUri;
    }

    /**
     * @return the declaringClass
     */
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public boolean isAccessible() {
        return field != null;
    }
    
    /**
     * @return the functional
     */
    public boolean isFunctional() {
        return functional;
    }

    /**
     * @param functional the functional to set
     */
    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer("(");
        sb.append(this.getClass().getSimpleName());
        sb.append(": ");
        sb.append("Field: ");
        sb.append( field != null ? field.toGenericString() : "(null)" );
        sb.append(" URI: ");
        sb.append(propUri);
        sb.append(" )");

        return sb.toString();
    }

    /**
     * 
     * @return the Type of Property Access e.g. field or Setter/getter-Pair
     */
    public PropertyAccessType getAccess() {
        return PropertyAccessType.FIELD;
    }

    public Class<?> getType(){
        return this.field.getType();
    }
    
    /**
     * @return the Field if this Property is accessed via fields otherwise {@code null}
     */
    public Field getField() {
        return this.field;
    }

}


