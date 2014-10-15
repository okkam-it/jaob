package com.yoshtec.owl.testclasses.bucket;

import com.yoshtec.owl.annotations.OwlClass;

@OwlClass(uri="http://www.yoshtec.com/ontology/test/Bucket#Stuff")
public class Stuff {

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stuff@").append(Integer.toHexString(this.hashCode())).append(": ");
        return sb.toString();
    }
}
