package com.yoshtec.owl.testclasses.brain;

import com.yoshtec.owl.annotations.OwlRegistry;

@OwlRegistry
public class ObjectFactory {
    
    public Brain createBrain(){
        return new Brain();
    }
    
}
