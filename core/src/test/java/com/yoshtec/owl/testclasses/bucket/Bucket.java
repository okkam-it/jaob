package com.yoshtec.owl.testclasses.bucket;

import java.util.ArrayList;
import java.util.List;

import com.yoshtec.owl.XsdType;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlDataTypes;
import com.yoshtec.owl.annotations.OwlObjectProperty;
import com.yoshtec.owl.annotations.dprop.OwlFunctionalDataProperty;

@OwlClass(uri="http://www.yoshtec.com/ontology/test/Bucket#Bucket")
public class Bucket {
	
	@OwlDataProperty(uri="http://www.yoshtec.com/ontology/test/Bucket#Engraving")
	@OwlDataType(uri=XsdType.XSD_STRING_URI)
	private List<String> engraving = null; 
	
	@OwlDataProperty(uri="http://www.yoshtec.com/ontology/test/Bucket#Material")
	@OwlDataType(uri=XsdType.XSD_STRING_URI)
	@OwlFunctionalDataProperty
	private String material = null;
	
	@OwlObjectProperty(uri="http://www.yoshtec.com/ontology/test/Bucket#Contains")
	@OwlDataTypes({
		@OwlDataType(uri="http://www.yoshtec.com/ontology/test/Bucket#Stone"),
		@OwlDataType(uri="http://www.yoshtec.com/ontology/test/Bucket#Stuff")
		})
	private List<Object> contains = null;

	public List<String> getEngraving() {
		if(engraving == null)
			engraving = new ArrayList<String>();
		return engraving;
	}

	public void setEngraving(List<String> engraving) {
		this.engraving = engraving;
	}
	
	public void addEngraving(String s) {
		if(engraving == null)
			engraving = new ArrayList<String>();
		engraving.add(s);
		
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public List<Object> getContains() {
		if(contains == null)
			contains = new ArrayList<Object>();
		return contains;
	}

	public void setContains(List<Object> contains) {
		this.contains = contains;
	}
	
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bucket@").append(Integer.toHexString(this.hashCode())).append(": ");
        sb.append("material='").append(material).append("'; ");
        
        sb.append("engraving='");
        for( String s : engraving ){
            sb.append("e='").append(engraving).append("', ");
        } 
        sb.append("';");
        
        return sb.toString();
    }
    
	
}
