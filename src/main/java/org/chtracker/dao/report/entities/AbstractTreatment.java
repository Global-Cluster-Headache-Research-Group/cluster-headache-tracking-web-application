package org.chtracker.dao.report.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.chtracker.dao.profile.Patient;

@MappedSuperclass
public abstract class AbstractTreatment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;

	@NotNull
	private LocalDateTime started;
	private LocalDateTime stopped;

	@ManyToOne(optional = false)
	private Patient patient;

	@Positive
	private int doze;

	@Size(max = 1000)
	private String comments;

	AbstractTreatment() {
	}

	public LocalDateTime getStarted() {
		return started;
	}

	public void setStarted(LocalDateTime started) {
		this.started = started;
	}

	public LocalDateTime getStopped() {
		return stopped;
	}

	public void setStopped(LocalDateTime stopped) {
		this.stopped = stopped;
	}

	public int getDoze() {
		return doze;
	}

	public void setDoze(int doze) {
		this.doze = doze;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Patient getPatient() {
		return patient;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public int hashCode() {
		return Objects.hash(patient, started);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractTreatment other = (AbstractTreatment) obj;
		return Objects.equals(patient, other.patient) && Objects.equals(started, other.started);
	}
	
	

}
