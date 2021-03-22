package com.yoshtec.owl.marshall;

import com.yoshtec.owl.testclasses.brain.Brain;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

public class MarshallerTest extends BaseReadWriteTest {
  @Test
  public void testMarshallerBrain1() throws Exception {
    Brain brain = new Brain();
    brain.setWeight(34.5f);
    ArrayList<Brain> obj = new ArrayList<Brain>();
    obj.add(brain);

    Marshaller marshaller = new Marshaller();

    marshaller.marshal(obj, IRI.create("BrainInds.owl"),
        IRI.create(tempFolder.newFile("BrainInds1.owl")));
  }



  @Test(expected = MarshalException.class)
  public void testError1() throws Exception {
    Collection<Object> col = new ArrayList<Object>();

    // add a non marshallable Object
    col.add(new Object());

    Marshaller m = new Marshaller();
    m.marshal(col, IRI.create("xxxxx"), true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError2() throws Exception {
    Marshaller m = new Marshaller();
    m.marshal(null, (IRI) null, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError3() throws Exception {
    Marshaller m = new Marshaller();
    m.marshal(null, (OWLOntology) null, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testError4() throws Exception {
    Marshaller m = new Marshaller();
    m.marshal(null, IRI.create("ahkaks"), (Writer) null, true);
  }

}
