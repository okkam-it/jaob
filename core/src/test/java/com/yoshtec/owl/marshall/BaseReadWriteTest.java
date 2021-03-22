package com.yoshtec.owl.marshall;

import java.io.File;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class BaseReadWriteTest {

  public final static String NS_MATRYOSHKA = "http://www.yoshtec.com/ontology/test/matryoshka";
  public final static String NS_GLASS = "http://www.yoshtec.com/ontology/test/Glass";

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  /** number of objects to create */
  protected final static int MAX_OBJ = 20000;
  protected static final int MAX_OBJ_SMALL = 2;// 500

  protected File getMatryoshkaOwlFile() {
    return new File("src/test/resources/matryoshka.owl");
  }

  protected File getBucketOwlFile() {
    return new File("src/test/resources/bucket.owl");
  }

  protected File getGlass1OwlFile() {
    return new File("src/test/resources/Glass1.owl");
  }
}
