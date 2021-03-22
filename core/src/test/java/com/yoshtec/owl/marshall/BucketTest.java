package com.yoshtec.owl.marshall;

import com.yoshtec.owl.testclasses.bucket.Bucket;
import com.yoshtec.owl.testclasses.bucket.Material;
import com.yoshtec.owl.testclasses.bucket.Stone;
import com.yoshtec.owl.testclasses.bucket.Stuff;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

/**
 * Test cases with the {@link Bucket} classes.
 * 
 * @author malottki
 * 
 */
public class BucketTest extends BaseReadWriteTest {

  @Test
  public void testMarshallerBucket1() throws Exception {

    Bucket bucket = new Bucket();
    bucket.setMaterial(Material.IRON.toString());
    bucket.addEngraving("One to Hold them all");
    bucket.addEngraving("Lots of stuff shall be in here");

    Stone stone1 = new Stone();
    stone1.setWeight(22);
    stone1.setDate_found(new GregorianCalendar());

    Stone stone2 = new Stone();
    stone2.setWeight(22);
    stone2.setDate_found(new GregorianCalendar());

    bucket.getContains().add(stone1);
    bucket.getContains().add(stone2);

    // Create a collection to store the objects
    // to marshall, all contained Objects will be a
    // automatically marshalled
    ArrayList<Object> a = new ArrayList<Object>();
    a.add(bucket);

    Marshaller marshaller = new Marshaller();
    File file = tempFolder.newFile("BucketInds1.owl");
    marshaller.marshal(a, IRI.create("BucketInds.owl"), IRI.create(file));

  }

  @Test
  public void testMarshallerBucket2() throws Exception {

    Bucket bucket = new Bucket();
    bucket.setMaterial(Material.GOLD.toString());
    bucket.addEngraving("One to Hold them all");

    for (int i = 0; i < MAX_OBJ; i++) {
      Stone stone = new Stone();
      stone.setWeight(i);
      stone.setDate_found(new GregorianCalendar());
      bucket.getContains().add(stone);
    }

    // Create a collection to store the objects
    // to marshall, all contained Objects will be a
    // automatically marshalled
    ArrayList<Object> a = new ArrayList<Object>();
    a.add(bucket);

    Marshaller marshaller = new Marshaller();
    File file = tempFolder.newFile("BucketInds2.owl");
    marshaller.marshal(a, IRI.create("BucketIndividualsLots.owl"), IRI.create(file));

  }

  @Test
  public void testUnMarshallerBucket1() throws Exception {
    UnMarshaller un = new UnMarshaller();

    un.registerClass(Bucket.class);
    un.registerClass(Stone.class);
    un.registerClass(Stuff.class);

    final File owlFile = getBucketOwlFile();
    Collection<Object> objects = un.unmarshal(IRI.create(owlFile));

    for (Object obj : objects) {
      System.out.println(obj.toString());
    }

  }

  @Test
  public void testUnMarshallerBucket2() throws Exception {
    UnMarshaller un = new UnMarshaller();

    un.registerClass(com.yoshtec.owl.testclasses.bucket.ObjectFactory.class);

    final File owlFile = getBucketOwlFile();
    final IRI iri = IRI.create(owlFile);
    Collection<Object> objects = un.unmarshal(iri);

    for (Object obj : objects) {
      System.out.println(obj.toString());
    }

  }

  @Ignore("the function under test is not implemented yet")
  @Test
  public void testUnMarshallerBucketByName() throws Exception {
    UnMarshaller un = new UnMarshaller();

    un.registerClass(Bucket.class);
    un.registerClass(Stone.class);
    un.registerClass(Stuff.class);

    final File owlFile = getBucketOwlFile();
    Bucket buck = un.unmarshall(IRI.create(owlFile),
        IRI.create("http://www.yoshtec.com/ontology/test/Bucket#MyPrecious"));

    System.out.println(buck.getMaterial());
  }
}
