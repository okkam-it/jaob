package com.yoshtec.owl.marshall;

import com.yoshtec.owl.testclasses.enumt.Glass;
import com.yoshtec.owl.testclasses.enumt.GlassColor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class GlassTest extends BaseReadWriteTest {

  @Test
  public void testMarshallerGlass1() throws Exception {
    Glass glass = new Glass();
    glass.setName("Coke");
    glass.setCol(GlassColor.GREEN);

    List<Glass> l = new ArrayList<Glass>();
    l.add(glass);

    Marshaller marshaller = new Marshaller();
    File file = tempFolder.newFile("Glass1.owl");
    marshaller.marshal(l, IRI.create("Glass1.owl"), IRI.create(file));

    UnMarshaller un = new UnMarshaller();
    un.addUriMapping(IRI.create(NS_GLASS), IRI.create(file));
    un.registerClass(Glass.class);
    un.registerClass(GlassColor.class);
    Collection<Object> objects = un.unmarshal(IRI.create(file));

    for (Object obj : objects) {
      System.out.println(obj.toString());
    }
  }
  @Test
  public void testMarshallerGlass2() throws Exception {
    Glass glass = new Glass();
    glass.setName("Coke");
    glass.setCol(GlassColor.BROWN);

    List<Glass> l = new ArrayList<Glass>();
    l.add(glass);

    Marshaller marshaller = new Marshaller();
    File file = tempFolder.newFile("Glass2.owl");
    marshaller.marshal(l, IRI.create("Glass2.owl"), IRI.create(file));
  }



}
