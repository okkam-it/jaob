Introduction
========

## Badges

[![license](https://img.shields.io/badge/license-LGPL_3.0-blue.svg)](https://raw.githubusercontent.com/MarquezProject/marquez/main/LICENSE)
[![maven](https://img.shields.io/maven-central/v/org.okkam.owl/jaob.svg)](https://search.maven.org/search?q=g:org.okkam.owl%20a:jaob)

// First version of README - taken from (http://wiki.yoshtec.com/jaob)

While Testing some Java OWL-APIs for filling data (individuals) into OWL ontologies I figured, that it is quite hard to do so. Especially if you already have a Java object data structure in your project and just want to put out some data. As I worked with JAXB and JPA which make use of annotations, I thought to myself that such kind of binding could be used to save Java objects to ontologies. Here is the solution (in a very early alpha stage) I came up with.

In the current alpha version the following things kinda work:

* Marshalling annotated objects to an ontology
* Unmarshal OWL Ontology Individuals to their corresponding Java objects
* Generate Java code (Interfaces & Classes) from an Ontology with annotations for marshalling and unmarshalling.
* Generate an Ontology with the definition of classes and properties from the annotated Java classes. (Currently still has a lot of shortcommings!)

Dependencies
--------

* Java 7 (or I guess it could be run using Java 1.5 plus JAXB)
* OWL-API for reading and writing ontologies.
* slf4j Java Simple Logging Facade and an implementation thereof. I use logback, but log4j could be used as well.
* CodeModel for generating Java code from ontologies, it is not necessary for marshalling and unmarshalling.
* OWL Concepts understood by the System

Classes
--------
* owl:Thing
* Named Classes
* ObjectUnionOf
* Properties (Data and Object)
* Functional Properties
* Inverse (only during code generation)
* Individuals for marshalling and unmarshalling
* Annotations:
    * Comments
    * Labels
    * Imports
    
OWL Concepts not understood and not currently handled
--------

* Classes
* ClassIntersectionOf
* Individuals as Class operators (ObjectOneOf)
* Sub Properties (Data and Object) which means they are currently treated as normal Properties without any special relation
* Annotations
* Individuals
* Individuals as constants
* Everything else not specifically mentioned in in the understood section ;)

License
--------
GNU Lesser General Public License
Or if you are interested another term to negotiated with me (and the authors to come).

Usage
--------

As Jaob is inspired mainly by JAXB it uses Annotations to map Java objects to OWL Individuals.

# Annotate Java Beans #

* __Classes__

    A Java Class or Interface can be mapped to a OWL Class via the @OwlClass Annotation:
```java
@OwlClass(uri="http://MyOntoUri#MyClass")
public class MyClass{
    //...
}
```
* __Properties__
    
    A __Data Property__ can be mapped to a class variable (later should be possible via setter getter pairs two):
```java
@OwlClass(uri="http://MyOntoUri#MyClass")
public class MyClass{
 
    @OwlDataProperty(uri="http://MyOntoUri#MyNameProp")
    @OwlFunctionalDataProperty
    @OwlDatatype(uri="http://www.w3.org/2001/XMLSchema#string")
    private String name = null;
    //...
}
```

    By Using the data type annotation the marshaller knows how to set the the value in the ontology. It is currently not possible to use more than one datatype for data properties.
    
    You can also use lists or collections in general for Properties:
```java
@OwlClass(uri="http://MyOntoUri#MyClass")
public class MyClass{
 
    @OwlDataProperty(uri="http://MyOntoUri#MyNickNameProp")
    @OwlDatatype(uri="http://www.w3.org/2001/XMLSchema#string")
    private List<String> nicknames = null;
    //...
}
```
    The same holds for __Object Properties__:
```java
@OwlClass(uri="http://MyOntoUri#MyClass")
public class MyClass{
 
    @OwlObjectProperty(uri="http://MyOntoUri#MyObjectProp")
    @OwlDatatype(uri="http://MyOntoUri#MyClass")
    private List<MyClass> classes = null;
    //...
}
```
    A special case is the name of the Individual, usually in an RDF representation of an OWL rdf:about or rdf:ID which can be handled as well:
```java
@OwlClass(uri="http://MyOntoUri#MyClass")
public class MyClass{
 
    @OwlIndividualId
    private String name = null;
    //...
}
```

# Marshalling an Unmarshalling #

* Marshalling can be done through the marshaller:
```java
Marshaller marshaller = new Marshaller();
marshaller.marshal(objectscollection , URI.create("MyOntology.owl"), 
            (new File("MyOntology.owl")).toURI());
```

* Unmarshalling works the other way around:
```java
UnMarshaller unmarshaller = new UnMarshaller();
// need to register the classes that shall be instantiated before:
un.registerClass(MatryoshkaImpl.class);
Collection<Object> objects = unmarshaller.unmarshal(  , 
    URI.create("MyOntology.owl"), (new File("MyOntology.owl")).toURI());
```
   
    The registering of classes has the advantage, that you can create a complex Ontology, then run the code generator get a bunch of classes out of it, and then you could create a subclass of an generated class which then shall be instantiated instead of the original generated code (this could not be done with jaxb for instance). All you have to do for it is implement an Interface that is annotated with the @OwlClass annotation or subclass a class with this annotation.

# Code Generation #

```java
Codegen codegen = new Codegen();
 
// the java package to create the classes in
codegen.setJavaPackageName("matryoshkatest");
 
// Ontology loading parameters
codegen.setOntologyUri("http://www.yoshtec.com/ontology/test/matryoshka");
codegen.setOntologyPhysicalUri( new File("test/matryoshka.owl").toURI().toString());
 
// where to write the source to
codegen.setJavaSourceFolder(new File("otest"));
 
// will generate "indName" String fields with @OwlIndividualId annotation and implementations
codegen.setGenerateIdField(true);
codegen.setIdFieldName("indName");
 
// generate code
codegen.genCode();
```
