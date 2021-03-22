package com.yoshtec.owl.ontogen;

import com.yoshtec.owl.Const;
import com.yoshtec.owl.annotations.OwlOntology;
import com.yoshtec.owl.annotations.ontology.OwlImports;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper Class for reflection tasks on {@link Package}s.
 * 
 * @author Jonas von Malottki
 *
 */
class PackageReflector {

  private static final Logger LOG = LoggerFactory.getLogger(PackageReflector.class);

  /**
   * Returns the Collection of the imported URIs.
   * 
   * @param p a package
   * @return a Collection of Imported URIs
   */
  public Collection<URI> getPackageImports(Package p) {
    Collection<URI> ret = new ArrayList<URI>();
    if (p != null) {
      // if it has imports
      if (p.isAnnotationPresent(OwlImports.class)) {
        // extract the imports
        OwlImports oimports = p.getAnnotation(OwlImports.class);
        // for every URI
        for (String uristr : oimports.uris()) {
          try {
            // try to create it
            URI uri = URI.create(uristr);
            // and finally add it to the Ontology as an Import
            ret.add(uri);
            LOG.debug("found import URI '{}' from Package '{}' to ontology", uristr, p);
          } catch (IllegalArgumentException e) {
            LOG.warn("malformed OwlImport URI '{}' in Package '{}'", uristr, p);
          }
        }
      }
    }
    return ret;
  }

  public URI getPackageUri(Package p) {
    if (p != null) {
      if (p.isAnnotationPresent(OwlOntology.class)) {
        OwlOntology onto = p.getAnnotation(OwlOntology.class);
        return URI.create(onto.uri());
      }
    }
    return null;
  }

  public Package findOntologyPackage(Class<?> c) {
    Package p = c.getPackage();

    while (p != null) {
      LOG.debug("Inspecting Package {} for URI", p);

      if (p.isAnnotationPresent(OwlOntology.class)) {
        OwlOntology onto = p.getAnnotation(OwlOntology.class);

        if (onto.uri().equals(Const.DEFAULT_ANNOTATION_STRING)) {
          throw new IllegalStateException(
              "Default URI was specified, which is illegal for a Ontology uri");
        }

        LOG.debug("Found URI {} on Package {}", onto.uri(), p);
        return p;

      } // else

      // no Ontology URI found go to the next upper level Package
      int lastdot = p.getName().lastIndexOf('.');
      if (lastdot < 0) {
        // no next level package
        LOG.info("Cannot find Ontology URI for Package {}", p);
        return null;
      }
      String topPackage = p.getName().substring(0, lastdot);
      p = Package.getPackage(topPackage);
    }
    LOG.debug("Was unable to find a Ontology URI for Class '{}'", c);
    return null;
  }

}
