package com.yoshtec.owl.annotations.classes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <a href="http://www.w3.org/2007/OWL/wiki/Syntax#Disjoint_Union_of_Class_Expression"> OWL Spec:
 * DisjointUnion</a>.
 * <p>
 * TODO: how use this in java?
 * </p>
 * <blockquote> A disjoint union axiom DisjointClasses( C CE1 ... CEn ) states that a class C is a
 * disjoint union of the class expressions CEi, 1 ≤ i ≤ n, all of which are mutually disjoint with
 * each other. Such axioms are sometimes referred to as covering axioms, as they state that the
 * extensions of all CEi exactly cover the extension of C. Thus, each instance of C must be an
 * instance of exactly one CEi, and each instance of CEi must be an instance of C. Each such axiom
 * is a syntactic shortcut for the following two axioms: <br>
 * <code>
 * EquivalentClasses( C UnionOf( CE1 ... CEn ) )
 * DisjointClasses( CE1 ... CEn )
 * </code> </blockquote>
 * 
 * @author Jonas von Malottki
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OwlDisjointUnion {
  /**
   * The list of disjoint classes.
   */
  String[] disjointClasses();
}
