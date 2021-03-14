package org.chtracker.dao.report;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.chtracker.dao.DataConfiguration;
import org.chtracker.dao.profile.Patient;

@Entity
@Table(
		schema = DataConfiguration.REPORT_SCHEMA_NAME, 
		uniqueConstraints = { @UniqueConstraint(
				name = "attack_uniq",
				columnNames = { "started", "patient_id" }
				) 
		})
public class Attack {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "attack_seq")
	int id;

	@NotNull
	private LocalDateTime started;
	private LocalDateTime stopped;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "attack__patient_fk"))
	private Patient patient;
	@Min(1)
	@Max(10)
	private int maxPainLevel;
	private Boolean whileAsleep;
	@Size(max = 1000)
	private String comments;

	Attack() {
		// needed for Hibernate (we can use private, but it will trigger Unused
		// constructor warning
	}

	public Attack(LocalDateTime started, LocalDateTime stopped, Patient patient, int maxPainLevel, Boolean whileAsleep, String comments) {
		this.started = started;
		this.stopped = stopped;
		this.patient = patient;
		this.maxPainLevel = maxPainLevel;
		this.whileAsleep = whileAsleep;
		this.comments = comments;
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

	public int getMaxPainLevel() {
		return maxPainLevel;
	}

	public void setMaxPainLevel(int maxPainLevel) {
		this.maxPainLevel = maxPainLevel;
	}

	public Boolean isWhileAsleep() {
		return whileAsleep;
	}

	public void setWhileAsleep(Boolean whileAsleep) {
		this.whileAsleep = whileAsleep;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result + ((started == null) ? 0 : started.hashCode());
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
		Attack other = (Attack) obj;
		if (!patient.equals(other.patient))
			return false;
		if (!started.equals(other.started))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Attack [patient=" + patient + ", started=" + started + "]";
	}

}
