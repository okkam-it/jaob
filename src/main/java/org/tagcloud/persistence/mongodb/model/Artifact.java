package org.tagcloud.persistence.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "artifacts")
@TypeAlias("artifact")
public class Artifact {
	enum ArtifactType {
		book, paint, picture
	}

	@Id
	private String id;
	// private String id dc.contributor.author;
	// dc.date.available;
	// dc.date.issued;
	@Indexed(unique = true)
	private String issn;

	@Indexed(unique = true)
	private String uri;
	// dc.description;
	// dc.language.iso;
	// dc.subject;
	// dc.subject;
	// dc.subject;
	// dc.subject;
	// dc.subject;
	// dc.title;
	private ArtifactType type;
	
	// GETTERS and SETTERS -------------------------------------
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIssn() {
		return issn;
	}
	public void setIssn(String issn) {
		this.issn = issn;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public ArtifactType getType() {
		return type;
	}
	public void setType(ArtifactType type) {
		this.type = type;
	}
}
