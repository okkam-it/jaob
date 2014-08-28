package com.yoshtec.owl.cf;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.yoshtec.owl.XsdTypeMapper;
import com.yoshtec.owl.testclasses.enumt.GlassColor;

public class TestEnumCF {

    private ClassFacade cf;
    
    @Before
    @SuppressWarnings("unchecked")
    public void beforeTest(){
        cf = new EnumCF(GlassColor.class, new XsdTypeMapper());
    }
    
    @Test
    public void testClass(){
        assertEquals("Classes are different", GlassColor.class, cf.getRepresentedClass());
    }
    
    @Test
    public void testUri(){
        //assertEquals("", URI.create("http://www.yoshtec.com/ontology/test/Glass#GlassColor"), cf.getClassUris().iterator().next() );
        assertEquals("", URI.create("#GlassColor"), cf.getClassUris().iterator().next() );
    }
    
}
