package com.yoshtec.owl.cf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlClassImplementation;

public class ClassFacadeFactory {

    private static final Logger log = LoggerFactory
            .getLogger(ClassFacadeFactory.class);
    
    private final XsdTypeMapper typeMapper;
    
    public ClassFacadeFactory(XsdTypeMapper typeMapper){
        if(typeMapper == null){
            this.typeMapper = new XsdTypeMapper();
        } else {
            this.typeMapper = typeMapper;
        }
    }
    
    public ClassFacadeFactory(){
        this.typeMapper = new XsdTypeMapper();
    }
    
    public XsdTypeMapper getTypeMapper() {
        return this.typeMapper;
    }
    
    
    public ClassFacade createClassFacade(Class<?> clazz){
        if( !(clazz.isAnnotationPresent(OwlClass.class) ||
            clazz.isAnnotationPresent(OwlClassImplementation.class)) ){
            //throw new IllegalArgumentException("Class" + clazz.getSimpleName() + "is not a OwlClass or OwlClassImplementation" );
        }
            
        
        if( clazz.isAnnotation() ){
            log.warn("Found annotation '{}' but cannot handle it", clazz);
            return null;
        } else if( clazz.isEnum() ){
            return new EnumCF((Class<Enum<?>>)clazz, typeMapper);
        } else {
            return new SimpleClassFacade(clazz, typeMapper);
        }
        
    }
    
}
