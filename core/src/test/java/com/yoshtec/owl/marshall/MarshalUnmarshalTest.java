package com.yoshtec.owl.marshall;

import com.yoshtec.owl.testclasses.matryoshka.Matryoshka;
import com.yoshtec.owl.testclasses.matryoshka.MatryoshkaImpl;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class MarshalUnmarshalTest extends BaseReadWriteTest {

  @Test
  public void testRoundTrip() throws Exception {
    final IRI tmpuri = IRI.create(tempFolder.newFile("Mtemp.owl"));
    {// create
      Matryoshka m1 = new MatryoshkaImpl("TheOne");
      m1.setColor("SoylentGreen");
      m1.setSize(MAX_OBJ_SMALL + 1);

      Matryoshka ml = m1;
      for (int i = MAX_OBJ_SMALL; i > 0; i--) {
        // creating new Matryoshka
        Matryoshka tmp = new MatryoshkaImpl("No" + String.format("%05d", i));
        tmp.setSize(i);
        tmp.setColor("pink" + i);
        ml.setContains(tmp);
        tmp.setContained_in(ml);
        ml = tmp;
      }

      ArrayList<Object> a = new ArrayList<Object>();
      a.add(m1);

      Marshaller marshaller = new Marshaller();
      try {
        marshaller.marshal(a, IRI.create("MatryoshkaInds.owl"), tmpuri);
      } catch (Exception e) {
        e.printStackTrace();
      }
      marshaller = null;
    }

    { // read

      UnMarshaller un = new UnMarshaller();
      un.addUriMapping(IRI.create(NS_MATRYOSHKA), IRI.create(getMatryoshkaOwlFile()));
      un.registerClass(MatryoshkaImpl.class);
      Collection<Object> objects = un.unmarshal(tmpuri);

      Assert.assertEquals(MAX_OBJ_SMALL + 1, objects.size());

      for (Object object : objects) {
        System.out.println(object.toString());
      }

      System.out.println("Unmarshalled ");
    }

  }

}
