package com.yoshtec.owl.testclasses.bucket;

import java.util.Calendar;

import com.yoshtec.owl.XsdType;
import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlDataProperty;
import com.yoshtec.owl.annotations.OwlDataType;
import com.yoshtec.owl.annotations.OwlObjectProperty;
import com.yoshtec.owl.annotations.oprop.OwlInverseObjectProperty;

@OwlClass(uri="http://www.yoshtec.com/ontology/test/Bucket#Stone")
public class Stone {

	@OwlDataProperty(uri="http://www.yoshtec.com/ontology/test/Bucket#DateFound")
	@OwlDataType(uri=XsdType.XSD_DATETIME_URI)
	Calendar date_found = null;
	
	@OwlDataProperty(uri="http://www.yoshtec.com/ontology/test/Bucket#weight")
	@OwlDataType(uri=XsdType.XSD_INT_URI)
	Integer weight = null;
	
	@OwlObjectProperty(uri="http://www.yoshtec.com/ontology/test/Bucket#is_in_Bucket")
	@OwlInverseObjectProperty(inverse="http://www.yoshtec.com/ontology/test/Bucket#Contains")
	Bucket contained_in = null;

	public Calendar getDate_found() {
		return date_found;
	}

	public void setDate_found(Calendar date_found) {
		this.date_found = date_found;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Bucket getContained_in() {
		return contained_in;
	}

	public void setContained_in(Bucket contained_in) {
		this.contained_in = contained_in;
	}
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Stone@").append(Integer.toHexString(this.hashCode())).append(": ");
	    sb.append("datefound='").append(date_found).append("'; ");
	    sb.append("weight='").append(weight).append("'; ");
	    
	    sb.append("contained_in='");
	    if(contained_in == null){
	        sb.append("null");
	    } else {
	        sb.append(contained_in.getClass().getSimpleName()).append("@").append(Integer.toHexString(contained_in.hashCode()));
	    }
	    sb.append("'; ");
	    
	    
	    
	    return sb.toString();
	}
	
}
