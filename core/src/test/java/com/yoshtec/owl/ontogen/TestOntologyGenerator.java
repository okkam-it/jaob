package com.yoshtec.owl.ontogen;

import java.io.File;

import org.junit.Test;

public class TestOntologyGenerator {

    
    private void execute(Class<?> clazz, String filename) throws Exception{
        OntologyGenerator ontogen = new OntologyGenerator();
        ontogen.setOwlRegistryClass(clazz);
        ontogen.genOntology();
        ontogen.saveOntology(new File(filename));
    }
    
    @Test
    public void testBrain() throws Exception {
        execute(com.yoshtec.owl.testclasses.brain.ObjectFactory.class, "otest/BrainDef.owl");
    }
    
    @Test
    public void testBucket() throws Exception {
        execute(com.yoshtec.owl.testclasses.bucket.ObjectFactory.class, "otest/BucketDef.owl");
    }
    
    @Test
    public void testGlass() throws Exception {
        execute(com.yoshtec.owl.testclasses.enumt.ObjectFactory.class, "otest/GlassDef.owl");
    }

    
}
