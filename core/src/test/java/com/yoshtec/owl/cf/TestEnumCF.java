package com.yoshtec.owl.cf;

import static org.junit.Assert.assertEquals;
import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.marshall.BaseReadWriteTest;
import com.yoshtec.owl.testclasses.enumt.GlassColor;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class TestEnumCF {

  private ClassFacade cf;

  @Before
  @SuppressWarnings("unchecked")
  public void beforeTest() {
    cf = new EnumCF(GlassColor.class, new XsdTypeMapper());
  }

  @Test
  public void testClass() {
    assertEquals("Classes are different", GlassColor.class, cf.getRepresentedClass());
  }

  @Test
  public void testUri() {
    assertEquals("", IRI.create(BaseReadWriteTest.NS_GLASS + "#GlassColor"),
        cf.getClassUris().iterator().next());
  }

}
