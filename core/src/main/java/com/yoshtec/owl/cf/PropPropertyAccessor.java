package com.yoshtec.owl.cf;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * Uses getters and Setters for the Task. 
 * 
 * @author Jonas von Malottki
 *
 */
public class PropPropertyAccessor implements PropertyAccessor{

    private final static Logger log = LoggerFactory.getLogger(PropertyAccessor.class); 

    private XsdTypeMapper typeMapper = null;
    
    // array or collection value?
    private boolean collection = false;
    private boolean array = false;
    
    private boolean functional = false;
    
    private Method getter = null;
    private Method setter = null;

    /** List of data types */
    private Set<IRI> dtUri = new HashSet<IRI>();
    private IRI propUri = null;
    private Class<?> declaringClass = null;
    
    /** allows to do lazy property setting, it is mostly used to be able to cache
     * Collections or arrays that would be expensive to copy */
    protected Map<Object, Object> valueCache = null;
    
    /** will be used to marshal an unmarshall literals */
    private XmlAdapter<String, Object> adapter = null;
    
    public PropPropertyAccessor(Method getter, Method setter, IRI propUri, XsdTypeMapper typeMapper) {
        this.init(getter, setter, propUri, null, typeMapper);
    }

    public PropPropertyAccessor(Method getter, Method setter, IRI propUri, XmlAdapter<?, ?> adapter, XsdTypeMapper typeMapper) {
        this.init(getter, setter, propUri, adapter, typeMapper);
    }
    
    private void init(Method getter, Method setter, IRI propUri, XmlAdapter<?, ?> adapter, XsdTypeMapper typeMapper){
        this.typeMapper = typeMapper;
        this.setter = setter;
        this.getter = getter;
        this.collection = Collection.class.isAssignableFrom(getter.getReturnType());
        this.array = getter.getReturnType().isArray();
        this.propUri = propUri;
        this.declaringClass = getter.getDeclaringClass();
        
        extractDataTypes();
        if(getter.getDeclaringClass() != getter.getDeclaringClass()){
            log.warn("Setter and getter defined in different classes {}, {}",getter.toGenericString(),setter.toGenericString());
        }
        
        this.functional = getter.isAnnotationPresent(OwlFunctionalDataProperty.class)
            || getter.isAnnotationPresent(OwlFunctionalObjectProperty.class);
    }

    private void extractDataTypes(){
        if(!extractDataTypes(getter)){
            dtUri.add(reflectOwlType(getter.getGenericReturnType()));
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
        
        if(this.isSingleValue()){
            valueCache.put(obj, value);
        } else {
            Collection c = (Collection)valueCache.get(obj);
            if(c == null){
                if(collection){
                    
                    // examine type of collection ?
                    Class<?> type = null;

                    type = getter.getReturnType();
                    
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
    @SuppressWarnings("unchecked")
    public void commit() throws IllegalAccessException, InvocationTargetException {

        if( valueCache == null )
            return; // nothing to to do in this case
        
        if( (!collection && setter == null ) || // only collections can be set via get
                ( collection && getter == null )){

            // TODO: this could also be done via bytecode enhancer 
            throw new IllegalStateException("No way to set the value");
        }

        for(Object obj : valueCache.keySet()){
            Object value = valueCache.get(obj);
            if(isSingleValue()){
                setter.invoke(obj, value);
            } else {
                if(array){
                    //set the new array
                    setter.invoke(obj, ((Collection<?>)value).toArray() );
                }

                if(collection){
                    if(getter != null){
                        Object val = getter.invoke(obj, (Object[])null);
                        if(val != null && val instanceof Collection){
                            Collection coll = Collection.class.cast(val);
                            coll.addAll((Collection<?>)value);
                            // no need to look further
                            continue;
                        } 
                    }
                    // will only reach here if the former one didn't succeed
                    if(setter != null ){
                        setter.invoke(obj, (Collection<?>)value);
                    }
                }
            }
            // finish here
            break;
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
        return getter.invoke(obj, (Object[])null);
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
        return setter != null;
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
        sb.append("Getter: ");
        sb.append( getter != null ? getter.toGenericString() : "(null)" );
        sb.append("Setter: ");
        sb.append( setter != null ? setter.toGenericString() : "(null)" );
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
        return PropertyAccessType.METHOD;
    }

    public Class<?> getType(){
        return getter.getReturnType();
    }

    /**
     * @return the Getter if this Property is accessed via setter/getter 
     * pairs {@code null} otherwise. 
     */
    public Method getGetter() {
        return this.getter;
    }

    /**
     * @return the setter Method if this Property is accessed via setter/getter 
     * pairs {@code null} otherwise. 
     */
    public Method getSetter() {
        return this.setter;
    }
}


