package org.chtracker.dao.report.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.chtracker.dao.DataConfiguration;
import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.chtracker.dao.profile.Patient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(schema = DataConfiguration.REPORT_SCHEMA_NAME)
public class AbortiveTreatment extends AbstractTreatment {

	@JsonIgnore
	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "abortive_treatment__attack_fk"))
	private Attack attack;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "abortive_treatment__abortive_treatment_fk"))
	private AbortiveTreatmentType abortiveTreatmentType;

	private Boolean successful;

	AbortiveTreatment() {
	}

	@SuppressWarnings("java:S107")
	public AbortiveTreatment(Patient patient, Attack attack, LocalDateTime started, LocalDateTime stopped, AbortiveTreatmentType abortiveTreatmentType, int doze, Boolean successful, String comments) {
		this.attack = attack;
		this.successful = successful;
		this.setStarted(started);
		this.setStopped(stopped);
		this.setPatient(patient);
		this.setAbortiveTreatmentType(abortiveTreatmentType);
		this.setDoze(doze);
		this.setComments(comments);
	}

	public AbortiveTreatment(Patient patient, Attack attack, AbortiveTreatmentType type, int doze, boolean successful) {
		this.attack = attack;
		this.successful = successful;
		this.setStarted(attack.getStarted());
		this.setPatient(patient);
		this.setAbortiveTreatmentType(type);
		this.setDoze(doze);
		this.setComments(attack.getComments());
	}

	public Attack getAttack() {
		return attack;
	}

	public void setAttack(Attack attack) {
		this.attack = attack;
	}

	public AbortiveTreatmentType getAbortiveTreatmentType() {
		return abortiveTreatmentType;
	}

	public void setAbortiveTreatmentType(AbortiveTreatmentType abortiveTreatmentType) {
		this.abortiveTreatmentType = abortiveTreatmentType;
	}

	public Boolean getSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(abortiveTreatmentType, attack);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof AbortiveTreatment))
			return false;
		AbortiveTreatment other = (AbortiveTreatment) obj;
		return Objects.equals(abortiveTreatmentType, other.abortiveTreatmentType) && Objects.equals(attack, other.attack);
	}

}
