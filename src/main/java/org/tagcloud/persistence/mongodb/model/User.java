package org.tagcloud.persistence.mongodb.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="user")
@TypeAlias("user")
public class User{

  public enum FamilyStatus {single, married, divorced, widowed };

  @Id
  private String id;

  /** http://purl.org/swum#family: Describes wether a person is a family man or not */
  private boolean family;

  /** http://purl.org/swum#familyStatus: Marital status of a person. Possible answers are "single" or "married", "divorced", "widowed" etc. */
  private FamilyStatus familyStatus;

  private UserContact contact;

  // GETTERS and SETTERS --------------------------------------
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public boolean isFamily() {
    return family;
  }
  public void setFamily(boolean family) {
    this.family = family;
  }
  public FamilyStatus getFamilyStatus() {
    return familyStatus;
  }
  public void setFamilyStatus(FamilyStatus familyStatus) {
    this.familyStatus = familyStatus;
  }
  public UserContact getContact() {
    return contact;
  }
  public void setContact(UserContact contact) {
    this.contact = contact;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }


}
