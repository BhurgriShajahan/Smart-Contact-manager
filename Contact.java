package com.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
//@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	@NotBlank(message = "Name fields is required !!")
	@Size(min = 3 , max = 20 , message = "min 3 and max 20 character are allowed !!")
	private String name;
	
	private String secondName;
	
	private String work;
	@NotBlank(message = "Email fields is required !!")
	private String email;
//	@Size(min = 11 , max = 11 , message = "only 11 character are allowed !!")
	@NotBlank(message = "Phone fields is required !!")
	private String phone;
	private String image;
	@Column(length = 5000)
	@Lob
	private String description;
	@ManyToOne
	@JsonIgnore
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.cId==((Contact)obj).getcId();
	}
	
	

	/*
	 * @Override public String toString() { return "Contact [cId=" + cId + ", name="
	 * + name + ", secondName=" + secondName + ", work=" + work + ", email=" + email
	 * + ", phone=" + phone + ", image=" + image + ", description=" + description +
	 * ", user=" + user + "]"; }
	 */
	

}
