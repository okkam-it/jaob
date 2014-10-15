package com.yoshtec.owl.testclasses.matryoshka;

import com.yoshtec.owl.annotations.OwlClass;

@OwlClass(uri="http://www.yoshtec.com/ontology/test/matryoshka#Matryoshka")
public interface Matryoshka {

	public String getColor();

	public void setColor(String color);
	
	public Integer getSize();

	public void setSize(Integer size);

	public Matryoshka getContained_in();

	public void setContained_in(Matryoshka contained_in);

	public Matryoshka getContains();

	public void setContains(Matryoshka contains);
}
