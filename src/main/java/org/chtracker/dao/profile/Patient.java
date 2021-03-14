package org.chtracker.dao.profile;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.chtracker.dao.DataConfiguration;

@Entity
@Table(schema = DataConfiguration.PROFILE_SCHEMA_NAME)
public class Patient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@NotNull
	@Size(min = 1, max = 25)
	@NotBlank
	private String login;
	@Email
	@NotBlank
	private String email;
	private LocalDate birthday;
	@NotNull
	private String name;
	private byte[] passwordHash;
	private short gender;
	private boolean isBlocked;
	private boolean isDeleted;

	Patient() {
		// needed for Hibernate (we can use private, but it will trigger Unused
		// constructor warning
	}

	public Patient(int id, String login, String email, LocalDate birthday, String name, byte[] passwordHash, short gender, boolean isBlocked, boolean isDeleted) {
		this.id = id;
		this.login = login;
		this.email = email;
		this.birthday = birthday;
		this.name = name;
		this.passwordHash = passwordHash;
		this.gender = gender;
		this.isBlocked = isBlocked;
		this.isDeleted = isDeleted;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}

	public short getGender() {
		return gender;
	}

	public void setGender(short gender) {
		this.gender = gender;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", login=" + login + "]";
	}

}
