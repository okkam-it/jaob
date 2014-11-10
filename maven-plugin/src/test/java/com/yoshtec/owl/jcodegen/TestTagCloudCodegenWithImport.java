package com.yoshtec.owl.jcodegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class TestTagCloudCodegenWithImport {
	
//	private static final File JAVA_SOURCE_FOLDER = new File("target/otest/tagcloud");
	private static final File JAVA_SOURCE_FOLDER = new File("/home/flavio/git/tagcloud-owl-proxy/src/main/java/");
	
	@Test
	public void testCodegenTagCloud() throws Exception{
		Codegen codegen = new Codegen();
		
		// the java package to create the classes in
		codegen.setJavaPackageName("org.tagcloud.owl.generated.application");
		
		// Ontology loading parameters
		codegen.setOntologyIri("http://models.okkam.org/tagcloud/application");
		codegen.setOntologyPhysicalIri( "http://models.okkam.org/tagcloud/application.owl");
		List<String> ignoreProperties = new ArrayList<>();
		ignoreProperties.add(OWLRDFVocabulary.OWL_TOP_DATA_PROPERTY.getIRI().toString());
		codegen.setIgnoreProperties(ignoreProperties);
//		codegen.setGenerateInterfaces(false);
		List<String> ignoredAbstractClassIRIs = new ArrayList<>();
		ignoredAbstractClassIRIs.add("http://models.okkam.org/tagcloud/application#SocialBeing");
		ignoredAbstractClassIRIs.add("http://purl.org/swum#Demographics");
		codegen.setIgnoredAbstractClassIRIs(ignoredAbstractClassIRIs);
		// where to write the source to
		codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
		
		// will generate "indName" String fields with @OwlIndividualId annotation and implementations
		codegen.setGenerateIdField(true);
		codegen.setIdFieldName("id");
		
		// generate code
		codegen.genCode();
	}
}
