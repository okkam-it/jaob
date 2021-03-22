package com.yoshtec.owl.jcodegen;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JDeclaration;
import java.net.URI;
import org.semanticweb.owlapi.model.OWLAnnotation;

/**
 * Allows to write a customization for the CodeModel.
 * <p>
 * The Codegen will call the {@code handleAnnotation(OWLAnnotation, JAnnotatable)} method after the
 * builtin annotation processing is done and if this annotation handler handles the uri.
 * </p>
 * 
 * @author Jonas von Malottki
 *
 */
public interface AnnotationHandler<E extends JAnnotatable & JDeclaration> {


  public boolean handlesUri(URI uri);


  public void handleAnnotation(OWLAnnotation owlannotation, E target);
}
