package com.yoshtec.owl.testclasses.brain;

import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlIndividualId;


@OwlClass
public class Brain {

	@OwlIndividualId
    protected String id;
	
    @OwlDataProperty
    protected Float weight;

    @Override
    public String toString() {
    	return "Brain individual "+id+": [weight: "+weight+"]";
    }
    
    public Float getWeight() {
        return this.weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    
}
