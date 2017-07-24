package com.selsoft.mouthpiece.models;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;

public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 576799940801180496L;
	
	private String firstName = null;
	private String lastName = null;
	
	@Id private int userId;
	private String emailId = null;
	private String password = null;
	
	private String address1 = null;
	private String address2 = null;
	private String address3 = null;
	private String city = null;
	private String state = null;
	private String country = null;
	private String zipCode = null;
	
	private String phoneNumber = null;
	
	private Error error = null;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = StringUtils.lowerCase(StringUtils.trim(emailId));
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Error getError() {
		return this.error;
	}
	
	public void setError(Error error) {
		this.error = error;
	}
	
	public boolean equals(Object object) {
		if(object != null && object instanceof User) {
			User thisObject = (User) object;
			return StringUtils.equalsIgnoreCase(this.getEmailId(), thisObject.getEmailId());
		}
		return false;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
