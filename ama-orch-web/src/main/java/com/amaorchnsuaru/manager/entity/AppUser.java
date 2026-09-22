package com.amaorchnsuaru.manager.entity;

import jakarta.persistence.Column;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@SQLRestriction("delete_datetime IS NULL")
@Table(name = "app_user")
public class AppUser extends AuditableEntity {

	@Id
	@Column(name = "user_account", nullable = false, length = 50)
	private String userAccount;

	@Column
	private String password;

	@Column(name = "display_name", length = 100)
	private String displayName;

	@Column(length = 50)
	private String role;

	@Column(name = "person_id")
	private Long personId;

	@Column(name = "google_subject", unique = true, length = 255)
	private String googleSubject;

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getGoogleSubject() {
		return googleSubject;
	}

	public void setGoogleSubject(String googleSubject) {
		this.googleSubject = googleSubject;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
