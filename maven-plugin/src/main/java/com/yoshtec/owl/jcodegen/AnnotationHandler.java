package com.yoshtec.owl.jcodegen;

import java.net.URI;

import org.semanticweb.owlapi.model.OWLAnnotation;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JDeclaration;

/**
 * Allows to write a customization for the CodeModel.
 * </br>
 * The Codegen will call the 
 * {@code handleAnnotation(OWLAnnotation, JAnnotatable)}
 * method after the builtin annotation processing is done and if this
 * annotation handler handles the uri. 
 * 
 * @author Jonas von Malottki
 *
 * @param <E>
 */
public interface AnnotationHandler<E extends JAnnotatable & JDeclaration>{

	
	public boolean handlesURI(URI uri);
	
	
	public void handleAnnotation(OWLAnnotation owlannotation, E target);
}
