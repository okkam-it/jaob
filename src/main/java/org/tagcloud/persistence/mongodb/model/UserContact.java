package org.tagcloud.persistence.mongodb.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.index.Indexed;


/**
 * Represents the contact information of a person (i.e. name, phone, address,
 * etc.)
 * 
 * Contain the "classes" from standard, but as data properties: 
 * <ul>
 *  <li>Name/<li>
 *  <li>Formated_Name/<li>
 *  <li>Mick_Name/<li>
 *  <li>Display_Name/<li>
 *  <li>Telephone_Number/<li>
 *  <li>E-mail/<li>
 *  <li>URL/<li>
 *  <li>Photo/<li>
 *  <li>UCI_Label/<li>
 *  <li>UCI_Additional_Data/<li>
 * </ul>
 * 
 * *
 */

public class UserContact {
	
	public enum Gender { male, female }
	
	/** http://purl.org/swum#givenName: First name of person */
	private String givenName;
	private String familyName;
	/** http://purl.org/swum#maidenName: Birth name of a person*/
	private String maidenName;//TODO needed?
	private String fullName;
	private String nickname;
	
	@Indexed(unique=true)
	private String mail;
	private String fax;
	@Indexed(unique=true)
	private String mobileNumber;
	private String telephoneNumber;
	
	private Gender gender;//TODO why not in Contact?

	// GETTERS and SETTERS --------------------------------------
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getMaidenName() {
		return maidenName;
	}

	public void setMaidenName(String maidenName) {
		this.maidenName = maidenName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}
	
}
