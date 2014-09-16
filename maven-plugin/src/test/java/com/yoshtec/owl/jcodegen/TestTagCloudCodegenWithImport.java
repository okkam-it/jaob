package com.yoshtec.owl.jcodegen;

import java.io.File;

import org.junit.Test;

public class TestTagCloudCodegenWithImport {
	
	@Test
	public void testCodegenMatryoshka() throws Exception{
		Codegen codegen = new Codegen();
		
		// the java package to create the classes in
//		codegen.setJavaPackageName("org.tagcloud.owl.generated");
		codegen.setJavaPackageName("org.tagcloud.owl.generated.artefact");
		
		// Ontology loading parameters
		codegen.setOntologyIri("http://models.okkam.org/tagcloud/artifact");
		codegen.setOntologyPhysicalIri( "http://models.okkam.org/tagcloud/artefact.owl");
		
		// where to write the source to
//		codegen.setJavaSourceFolder(new File("/tmp/jaob-test"));
		codegen.setJavaSourceFolder(new File("/home/flavio/git/playground/src/main/java/"));
		
		// will generate "indName" String fields with @OwlIndividualId annotation and implementations
		codegen.setGenerateIdField(true);
		codegen.setIdFieldName("id");
		
		// generate code
		codegen.genCode();
	}
}
