package com.yoshtec.owl.testclasses.enumt;

import java.io.File;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GlassParse {

    
    final static private Logger log = LoggerFactory.getLogger(GlassParse.class);
    
    @Test
    public void testGlassParsing() throws Exception {
        
        // create a manger for Ontologies
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();    

        // We need a data factory to create various object from.  Each ontology has a reference
        // to a data factory that we can use.
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument((new File("test/Glass1.owl")));
        boolean includeImportsClosure = true;
        for(OWLClass cl : ontology.getClassesInSignature(includeImportsClosure) ){
            log.debug("Class {}", cl);
            
            for(OWLClassExpression cl2 : cl.getEquivalentClasses(ontology)){
                log.debug("EClass :: {}", cl2);
                
                OWLClassExpressionVisitor vis = new OWLClassExpressionVisitor(){

                    @Override
                    public void visit(OWLClass desc) {
                    }

                    @Override
                    public void visit(OWLObjectIntersectionOf desc) {
                    }

                    @Override
                    public void visit(OWLObjectUnionOf desc) {
                    }

                    @Override
                    public void visit(OWLObjectComplementOf desc) {
                    }

                    @Override
                    public void visit(OWLObjectOneOf desc) {
                        log.debug("Individuals: {}" , desc.getIndividuals());
                    }

					@Override
					public void visit(OWLObjectSomeValuesFrom ce) {
					}

					@Override
					public void visit(OWLObjectAllValuesFrom ce) {
					}

					@Override
					public void visit(OWLObjectHasValue ce) {
					}

					@Override
					public void visit(OWLObjectMinCardinality ce) {
					}

					@Override
					public void visit(OWLObjectExactCardinality ce) {
					}

					@Override
					public void visit(OWLObjectMaxCardinality ce) {
					}

					@Override
					public void visit(OWLObjectHasSelf ce) {
					}

					@Override
					public void visit(OWLDataSomeValuesFrom ce) {
					}

					@Override
					public void visit(OWLDataAllValuesFrom ce) {
					}

					@Override
					public void visit(OWLDataHasValue ce) {
					}

					@Override
					public void visit(OWLDataMinCardinality ce) {
						
					}

					@Override
					public void visit(OWLDataExactCardinality ce) {
						
					}

					@Override
					public void visit(OWLDataMaxCardinality ce) {
					}

                    
                    
                };
                
                cl2.accept(vis);

                
            }
            
        }
        
    }
    
}
