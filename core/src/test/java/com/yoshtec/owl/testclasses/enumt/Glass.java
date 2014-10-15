package com.yoshtec.owl.testclasses.enumt;

import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlObjectProperty;

@OwlClass
public class Glass {

    @OwlDataProperty
    private String name;
    
    @OwlObjectProperty
    private GlassColor col = GlassColor.WHITE;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GlassColor getCol() {
        return this.col;
    }

    public void setCol(GlassColor col) {
        this.col = col;
    }
    
    private static final String TOSTRING_FORMAT = "Glass@%s : name=%s, color=%s"; 
    
    @Override
    public String toString() {
        return String.format(TOSTRING_FORMAT, Integer.toHexString(super.hashCode()), name, col );
    }
    
}
