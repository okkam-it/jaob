package com.yoshtec.owl.jcodegen;

import java.io.File;

import org.junit.Test;

public class TestTagCloudCodegen {
	
	@Test
	public void testCodegenMatryoshka() throws Exception{
		Codegen codegen = new Codegen();
		
		// the java package to create the classes in
		codegen.setJavaPackageName("org.tagcloud.owl.generated");
		
		// Ontology loading parameters
		codegen.setOntologyIri("http://www.tagcloud.com/ontology/test/tagcloud");
		codegen.setOntologyPhysicalIri( "file:///home/flavio/git/playground/src/test/resources/user-JAOB.owl");
		
		// where to write the source to
		codegen.setJavaSourceFolder(new File("/tmp/jaob-test"));
		
		// will generate "indName" String fields with @OwlIndividualId annotation and implementations
		codegen.setGenerateIdField(true);
		codegen.setIdFieldName("id");
		
		// generate code
		codegen.genCode();
	}
	
}
