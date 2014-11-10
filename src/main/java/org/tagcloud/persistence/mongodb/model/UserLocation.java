package org.tagcloud.persistence.mongodb.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_locations")
@TypeAlias("userlocation")
public class UserLocation {

	@Id
	private String id;
	private String name;
	private double[] location;
	private final String userId;

	public UserLocation(String userId) {
		this.userId = userId;	
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}
	
	// GETTERS and SETTERS ------------------------------------
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[] getLocation() {
		return location;
	}

	public String getUserId() {
		return userId;
	}

	public void setLocation(double latitude, double longitude) {
		this.location = new double[]{latitude, longitude};
	}
}
