package org.tagcloud.persistence.mongodb.model;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="locations")
@TypeAlias("location")
public class Location {
	    
	  @Id
	  private String id;
	  private String name;
	  private double[] location;
	  
	  @PersistenceConstructor
	  Location(String name, double[] location) {
	    this.name = name;
	    this.location = location;    
	  }
	  
	  public Location(String name, double x, double y) {
	    super();
	    this.name = name;
	    this.location = new double[] { x, y };    
	  }

	  public String getName() {
	    return name;
	  }

	  public double[] getLocation() {
	    return location;
	  }

	  @Override
	  public String toString() {
	    return "TagCloudArtifact [id=" + id + ", name=" + name + ", location="
	        + Arrays.toString(location) + "]";
	  } 
	}
