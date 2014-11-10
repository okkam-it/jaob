package org.tagcloud.persistence.mongodb.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="user_devices")
@TypeAlias("userdevice")
public class UserDevice {
	
	@Id
	private String id;
	private final String userId;

	public UserDevice(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}

	// GETTERS and SETTERS --------------------------------
	public String getUserId() {
		return userId;
	}
}
