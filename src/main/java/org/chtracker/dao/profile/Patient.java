package org.chtracker.dao.profile;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("profile\".\"patient")
public class Patient {
	@Id
	private int id;
	private String login;
	private String email;
	private LocalDate birthday;
	private String name;
	private byte[] password_hash;
	private short gender;
	private boolean isBlocked;
	private boolean isDeleted;

	public Patient(int id, String login, String email, LocalDate birthday, String name, byte[] password_hash, short gender, boolean isBlocked, boolean isDeleted) {
		this.id = id;
		this.login = login;
		this.email = email;
		this.birthday = birthday;
		this.name = name;
		this.password_hash = password_hash;
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

	public byte[] getPassword_hash() {
		return password_hash;
	}

	public void setPassword_hash(byte[] password_hash) {
		this.password_hash = password_hash;
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
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", login=" + login + "]";
	}

}
