package com.yoshtec.owl.marshall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import com.yoshtec.owl.testclasses.enumt.Glass;
import com.yoshtec.owl.testclasses.enumt.GlassColor;

public class GlassTest {

	private static final String OUT_DIR = "target/test";
    @Test
    public void testMarshallerGlass1() throws Exception {
        Glass glass = new Glass();
        glass.setName("Coke");
        glass.setCol(GlassColor.GREEN);
        
        List<Glass> l = new ArrayList<Glass>();
        l.add(glass);
        
        Marshaller marshaller = new Marshaller();
        
        File file = new File(OUT_DIR, "Glass1.owl");
		marshaller.marshal(l, IRI.create("Glass1.owl"), IRI.create(file));
    }
    
    @Test
    public void testMarshallerGlass2() throws Exception {
        Glass glass = new Glass();
        glass.setName("Coke");
        glass.setCol(GlassColor.BROWN);
        
        List<Glass> l = new ArrayList<Glass>();
        l.add(glass);
        
        Marshaller marshaller = new Marshaller();
        File file = new File(OUT_DIR, "Glass2.owl");
        marshaller.marshal(l, IRI.create("Glass2.owl"),IRI.create(file));
    }
    
    
    @Test
    public void testUnMarshallerBucket1() throws Exception {
        UnMarshaller un = new UnMarshaller();

        un.registerClass(Glass.class);
        un.registerClass(GlassColor.class);

        File file = new File(OUT_DIR, "Glass1.owl");
        Collection<Object> objects = un.unmarshal(IRI.create(file) );

        for(Object obj: objects){
            System.out.println(obj.toString());
        }

    }
    
}
