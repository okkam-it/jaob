package com.yoshtec.owl.testclasses.bucket;

import com.yoshtec.owl.annotations.OwlRegistry;

@OwlRegistry
public class ObjectFactory {

    public Bucket createBucket(){
        return new Bucket();
    }
    
    public Stone createStone(){
        return new Stone();
    }
    
    public Stuff createStuff(){
        return new Stuff();
    }
    
    public Material createMaterial(){
        return Material.GOLD;
    }
    
}
