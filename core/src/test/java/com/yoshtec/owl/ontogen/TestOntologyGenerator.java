package com.yoshtec.owl.ontogen;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestOntologyGenerator {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private void execute(Class<?> clazz, File file) throws Exception {
    OntologyGenerator ontogen = new OntologyGenerator();
    ontogen.setOwlRegistryClass(clazz);
    ontogen.genOntology();
    ontogen.saveOntology(file);
  }

  @Test
  public void testBrain() throws Exception {
    execute(com.yoshtec.owl.testclasses.brain.ObjectFactory.class,
        tempFolder.newFile("BrainDef.owl"));
  }

  @Test
  public void testBucket() throws Exception {
    execute(com.yoshtec.owl.testclasses.bucket.ObjectFactory.class,
        tempFolder.newFile("BucketDef.owl"));
  }

  @Test
  public void testGlass() throws Exception {
    execute(com.yoshtec.owl.testclasses.enumt.ObjectFactory.class,
        tempFolder.newFile("GlassDef.owl"));
  }

}
