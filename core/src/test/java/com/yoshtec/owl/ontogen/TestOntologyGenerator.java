package com.yoshtec.owl.ontogen;

import java.io.File;

import org.junit.Test;

public class TestOntologyGenerator {

	private static final String OUT_DIR = "target/test";
	
    private void execute(Class<?> clazz, File file) throws Exception{
        OntologyGenerator ontogen = new OntologyGenerator();
        ontogen.setOwlRegistryClass(clazz);
        ontogen.genOntology();
        ontogen.saveOntology(file);
    }
    
    @Test
    public void testBrain() throws Exception {
        execute(com.yoshtec.owl.testclasses.brain.ObjectFactory.class, new File(OUT_DIR, "BrainDef.owl"));
    }
    
    @Test
    public void testBucket() throws Exception {
        execute(com.yoshtec.owl.testclasses.bucket.ObjectFactory.class,new File(OUT_DIR, "BucketDef.owl"));
    }
    
    @Test
    public void testGlass() throws Exception {
        execute(com.yoshtec.owl.testclasses.enumt.ObjectFactory.class, new File(OUT_DIR, "GlassDef.owl"));
    }
    
}
