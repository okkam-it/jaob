package com.yoshtec.owl.testclasses.individual;

import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlIndividual;

@OwlClass
public class MyClass {

    @OwlIndividual
    public static final MyClass STATIC_IND = new MyClass(){
        { myname = "Lord Fothelsporth"; }
    };
    
    @OwlDataProperty
    protected String myname = "Lord Bitterburogh" ;
    
}
