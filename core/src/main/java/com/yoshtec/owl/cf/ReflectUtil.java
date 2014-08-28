package com.yoshtec.owl.cf;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoshtec.owl.Const;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlClassImplementation;
import com.yoshtec.owl.annotations.OwlOntology;

public final class ReflectUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectUtil.class);
    
    public static IRI getClassIri(Class<?> clazz){
    	//TODO ---------- check if generation still works after modification
    	OwlClass oc = null;
        if( clazz.isAnnotationPresent(OwlClass.class) ){
            oc = clazz.getAnnotation(OwlClass.class);
        }else if(clazz.isAnnotationPresent(OwlClassImplementation.class)){
        	Class<?> impl = clazz.getAnnotation(OwlClassImplementation.class).value()[0];
			oc = impl.getAnnotation(OwlClass.class);
        }
        return getIri(clazz, oc);
    }

	private static IRI getIri(Class<?> clazz, OwlClass oc) {
		if( oc.uri().equals(Const.DEFAULT_ANNOTATION_STRING) ){
		    if( clazz.getPackage().isAnnotationPresent(OwlOntology.class) ){
		        return IRI.create(
		                clazz.getPackage().getAnnotation(OwlOntology.class).uri()
		                + "#" +
		                clazz.getSimpleName());
		    }  
		    log.error("No Ontology Uri on Package {}", clazz.getPackage() );
		} else {
		    return IRI.create(oc.uri());
		}
		return null;
	}
    
    static List<Package> getSubPackages(Package p){
        List<Package> res = new ArrayList<Package>();
        String packname = p.getName();
        int last = packname.lastIndexOf('.');
        while( packname.length() > 0 && last > 0 ){
            packname = packname.substring(0, last - 1);
            Package pl = Package.getPackage(packname);
            if( pl != null ){
                res.add(p);
            }
        }
        
        return res;
    }
    
    public static String getBaseUri(Package p){
        if( p.isAnnotationPresent(OwlOntology.class) ){
            return p.getAnnotation(OwlOntology.class).uri();
        }
        return null;
    }
    
}
