package com.yoshtec.owl.jcodegen;

import static org.junit.Assert.*;
import org.junit.Test;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

public class SmallJModelTest {

    // unused 
	//@Test
	public void subInterface() throws Exception{
		JCodeModel jmod = new JCodeModel();
		
		// adding a test package
		JPackage pack = jmod._package("subInterface");
		
		// building an interface 
		JDefinedClass interface1 = pack._interface("Interface1");
		
		// adding a second interface
		JDefinedClass interface2 = pack._interface("Interface2");
		
		// Setting interface1 as superinterface for interface2 so that
		// code: public interface Interface2 extends Interface1;
		interface2._implements(interface1);
		
		// BOOM: exception: IllegalArgumentException: Unable to set the super class for an interface 
		
	}
	
	/**
	 * Adding nested Annotations in JModel
	 * @throws Exception
	 */
	// unused
	//@Test
	public void testNestedAnnotations() throws Exception{
		JCodeModel jmod = new JCodeModel();
		
		// adding a test package
		JPackage pack = jmod._package("testNestedAnnotations");
		
		// building an interface 
		JDefinedClass interface1 = pack._interface("Interface1");

		// adding annotations
		JDefinedClass annotation1 = pack._annotationTypeDeclaration("Annot1");
		JDefinedClass annotation2 = pack._annotationTypeDeclaration("Annot2");
		
		// adding a method for annotation2
		annotation1.method(JMod.NONE, String.class, "value");
		
		//adding a method which has an annotation as type to annotation1
		annotation2.method(JMod.NONE, annotation1.array(), "value");

		// add an annotation to the Interface
		JAnnotationArrayMember paramarray = interface1.annotate(annotation2).paramArray("value");
		paramarray.annotate(annotation1).param("value", "a");
		//paramarray.annotate(annotation1).param("value", "b");
		//paramarray.annotate(annotation1).param("value", "c");
		
		jmod.build(new SingleStreamCodeWriter(System.out));

	}
	
	// unused
	//@Test 
	public void testAnnotationImplements() throws Exception{
		JCodeModel jmod = new JCodeModel();
		
		// adding a test package
		JPackage pack = jmod._package("testAnnotationImplements");
		
		// building an interface 
		JDefinedClass interface1 = pack._interface("Interface1");

		// adding annotations
		JDefinedClass annotation1 = pack._annotationTypeDeclaration("Annot1");
		
		try{
			//this is perfectly legal in CodeModel:
			annotation1._implements(interface1);
			
			fail("No Exception was thrown for Illegal behavior");
		} catch ( IllegalArgumentException ie){
			
		}
		
		jmod.build(new SingleStreamCodeWriter(System.out));
	}

}
