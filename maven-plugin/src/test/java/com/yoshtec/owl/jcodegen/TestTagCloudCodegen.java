package com.yoshtec.owl.jcodegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class TestTagCloudCodegen {
	
	@Test
	public void testCodegenMatryoshka() throws Exception{
		Codegen codegen = new Codegen();
		
		// the java package to create the classes in
//		codegen.setJavaPackageName("org.tagcloud.owl.generated");
//		codegen.setOntologyIri("http://www.semanticweb.org/vfayet/ontologies/2013/5/untitled-ontology-36");
//		codegen.setOntologyPhysicalIri( "file:///home/flavio/git/playground/src/test/resources/user-JAOB.owl");
		
		codegen.setJavaPackageName("org.tagcloud.owl.user");
		// Ontology loading parameters
		codegen.setOntologyIri("http://www.semanticweb.org/vfayet/ontologies/2013/5/untitled-ontology-36");
		codegen.setOntologyPhysicalIri( "file:///home/flavio/git/playground/src/test/resources/UserProfile_20140919.owl");
		List<String> ignoreProperties = new ArrayList<>();
		ignoreProperties.add("http://purl.org/swum#self-esteem");
		ignoreProperties.add("http://purl.org/swum#self-respect");
		ignoreProperties.add(OWLRDFVocabulary.OWL_TOP_DATA_PROPERTY.getIRI().toString());
		codegen.setIgnoreProperties(ignoreProperties);
		
		// where to write the source to
//		codegen.setJavaSourceFolder(new File("/tmp/jaob-test"));
		codegen.setJavaSourceFolder(new File("/home/flavio/git/playground/src/main/java"));
		
		
		// will generate "indName" String fields with @OwlIndividualId annotation and implementations
		codegen.setGenerateIdField(true);
		codegen.setIdFieldName("id");
		
		// generate code
		codegen.genCode();
	}
	
}
