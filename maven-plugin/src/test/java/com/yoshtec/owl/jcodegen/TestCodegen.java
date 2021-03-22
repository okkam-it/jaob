package com.yoshtec.owl.jcodegen;

import java.io.File;
import org.junit.Ignore;
import org.junit.Test;

public class TestCodegen {

  private static final File JAVA_SOURCE_FOLDER = new File("target/otest");

  @Test
  public void testCodegenMatryoshka() throws Exception {
    Codegen codegen = new Codegen();

    // the java package to create the classes in
    codegen.setJavaPackageName("matryoshkatest");


    // Ontology loading parameters
    codegen.setOntologyIri("http://www.yoshtec.com/ontology/test/matryoshka");
    codegen
        .setOntologyPhysicalIri(new File("src/test/resources/matryoshka.owl").toURI().toString());

    // where to write the source to
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);

    // will generate "indName" String fields with @OwlIndividualId
    // annotation and implementations
    // codegen.setGenerateIdField(false);
    // codegen.setIdFieldName("id");

    // generate code
    codegen.genCode();
  }

  @Test
  public void testCodegenBucket() throws Exception {
    Codegen codegen = new Codegen();
    codegen.setJavaPackageName("buckettest");
    codegen.setOntologyIri("http://www.yoshtec.com/ontology/test/Bucket");
    codegen.setOntologyPhysicalIri(new File("src/test/resources/bucket.owl").toURI().toString());
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
    codegen.setGenerateIdField(false);
    codegen.genCode();
  }

  @Test
  public void testCodegenGlass() throws Exception {
    Codegen codegen = new Codegen();
    codegen.setJavaPackageName("glass");
    codegen.setOntologyIri("http://www.yoshtec.com/ontology/test/Glass");
    codegen.setOntologyPhysicalIri(new File("src/test/resources/Glass1.owl").toURI().toString());
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
    codegen.setGenerateIdField(false);
    codegen.setGenerateInterfaces(false);
    codegen.setJavaClassSuffix("");
    codegen.genCode();
  }

  @Ignore
  @Test
  public void testCodegenPizza1() throws Exception {
    Codegen codegen = new Codegen();
    codegen.setJavaPackageName("pizza1");
    codegen.setOntologyIri("http://www.co-ode.org/ontologies/pizza/pizza.owl");
    codegen.setOntologyPhysicalIri(new File("src/test/resources/pizza.owl").toURI().toString());
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
    codegen.setGenerateIdField(true);
    codegen.genCode();
  }

  @Test
  public void testCodegenIntersection() throws Exception {
    Codegen codegen = new Codegen();
    codegen.setJavaPackageName("intersect");
    codegen.setOntologyIri("http://www.yoshtec.com/test/intersect.owl");
    codegen.setOntologyPhysicalIri(new File("src/test/resources/intersect.owl").toURI().toString());
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
    codegen.setGenerateIdField(false);
    codegen.genCode();
  }

  @Test
  public void testCodegenUnion() throws Exception {
    Codegen codegen = new Codegen();
    codegen.setJavaPackageName("unionof");
    codegen.setOntologyIri("http://www.yoshtec.com/test/unionof.owl");
    codegen.setOntologyPhysicalIri(new File("src/test/resources/unionof.owl").toURI().toString());
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
    codegen.setGenerateIdField(false);
    codegen.genCode();
  }

  @Test(expected = CodegenException.class)
  public void testCodegenUnionIntersect() throws Exception {
    Codegen codegen = new Codegen();
    codegen.setJavaPackageName("unionintersect");
    codegen.setOntologyIri("http://www.yoshtec.com/test/unionintersect.owl");
    codegen.setOntologyPhysicalIri(
        new File("src/test/resources/unionintersect.owl").toURI().toString());
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
    codegen.setGenerateIdField(false);
    codegen.genCode();
  }

  /**
   * Tests multiple inheritance with classes schould generate an error
   */
  @Test(expected = CodegenException.class)
  public void testCodegenMultipleError() throws Exception {
    Codegen codegen = new Codegen();
    codegen.setJavaPackageName("multiple");
    codegen.setOntologyIri("http://www.yoshtec.com/test/multiple.owl");
    codegen.setOntologyPhysicalIri(new File("src/test/resources/multiple.owl").toURI().toString());
    codegen.setJavaSourceFolder(JAVA_SOURCE_FOLDER);
    codegen.setGenerateInterfaces(false);
    codegen.setGenerateIdField(false);
    codegen.genCode();
  }

}
