package com.yoshtec.owl.testclasses.enumt;

import com.yoshtec.owl.annotations.OwlRegistry;

@OwlRegistry
public class ObjectFactory {

    public Glass createGlass(){
        return new Glass();
    }
    
    
    public GlassColor createGlassColor(){
        return GlassColor.WHITE;
    }
}
