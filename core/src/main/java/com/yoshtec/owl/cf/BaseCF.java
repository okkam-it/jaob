package com.yoshtec.owl.cf;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.annotations.OwlOntology;
import com.yoshtec.owl.annotations.ontology.OwlImports;

abstract class BaseCF<T> implements ClassFacade {
    
    final static private Logger log = LoggerFactory.getLogger(BaseCF.class);
    
    /** the class this class facade represents */
    Class<T> clazz = null;
    
    protected IRI ontoBaseUri = null;
    
    /** Imported Uris from the Package */
    protected Set<IRI> importedUris = null;

    /** maps the Java classes to XsdTypes and vice versa */
    protected XsdTypeMapper typeMapper;

    /** List of URIs this Class represents in the Owl World 
     * which can be handled by this facade */
    protected Set<IRI> classuris = new HashSet<IRI>();

    BaseCF(Class<T> clazz, XsdTypeMapper typeMapper) {
        this.typeMapper = typeMapper;
        this.clazz = clazz;
    }

    protected void readPackage() {
    	final Package p = clazz.getPackage();
    	
    	log.trace("PACKAGE:");
    	log.trace(" P: {}", p.getName());
    
    	// Base uri
    	if(p.isAnnotationPresent(OwlOntology.class)){
    		OwlOntology a = p.getAnnotation(OwlOntology.class);
    		this.ontoBaseUri = IRI.create(a.uri());
    	}
//    	log.debug(" Package IRI = '{}'", this.ontoBaseUri);
    	
    	
    	// Imported Uris
    	if(p.isAnnotationPresent(OwlImports.class)){
    		OwlImports imp = p.getAnnotation(OwlImports.class);
    		for(String us : imp.uris()){
    		    log.trace(" Import IRI '{}'", us);
    		    importedUris.add(IRI.create(us));
    		}
    	}
    }

    public IRI getOntoBaseUri() {
    	return ontoBaseUri;
    }

    public XsdTypeMapper getTypeMapper() {
        return this.typeMapper;
    }

    public void setTypeMapper(XsdTypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public Set<IRI> getClassUris() {
    	return classuris;
    }
    
    public Set<IRI> getImportedUris(){
        return importedUris;
    }
    
    public boolean handlesClass(IRI uri){
        return classuris.contains(uri);
    }
    
    public Class<?> getRepresentedClass(){
        return this.clazz;
    }

}