package org.chtracker.dao.report;

import java.time.LocalDateTime;

import org.chtracker.dao.metadata.TreatmentType;
import org.chtracker.dao.profile.Patient;

public class AbortiveTreatment {

	private LocalDateTime started;
	private LocalDateTime stopped;
	private LocalDateTime attackStarted;

	private final Patient patient;
	private final TreatmentType treatmentType;
	private int doze;
	private Boolean successful;
	private String comments;

	public AbortiveTreatment(LocalDateTime started, LocalDateTime stopped, LocalDateTime attackStarted, Patient patient, TreatmentType treatmentType, int doze, Boolean successful, String comments) {
		this.started = started;
		this.stopped = stopped;
		this.attackStarted = attackStarted;
		this.patient = patient;
		this.treatmentType = treatmentType;
		this.doze = doze;
		this.successful = successful;
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attackStarted == null) ? 0 : attackStarted.hashCode());
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result + ((started == null) ? 0 : started.hashCode());
		result = prime * result + ((treatmentType == null) ? 0 : treatmentType.hashCode());
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
		AbortiveTreatment other = (AbortiveTreatment) obj;
		if (attackStarted == null) {
			if (other.attackStarted != null)
				return false;
		} else if (!attackStarted.equals(other.attackStarted))
			return false;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		if (started == null) {
			if (other.started != null)
				return false;
		} else if (!started.equals(other.started))
			return false;
		if (treatmentType == null) {
			if (other.treatmentType != null)
				return false;
		} else if (!treatmentType.equals(other.treatmentType))
			return false;
		return true;
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

	public LocalDateTime getAttackStarted() {
		return attackStarted;
	}

	public void setAttackStarted(LocalDateTime attackStarted) {
		this.attackStarted = attackStarted;
	}

	public int getDoze() {
		return doze;
	}

	public void setDoze(int doze) {
		this.doze = doze;
	}

	public Boolean getSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean successful) {
		this.successful = successful;
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

	public TreatmentType getTreatmentType() {
		return treatmentType;
	}
	
}
