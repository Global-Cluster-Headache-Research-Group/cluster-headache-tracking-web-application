package org.chtracker.dao.report;

import java.time.LocalDateTime;

import org.chtracker.dao.profile.Patient;

public class Attack {
	private LocalDateTime started;
	private LocalDateTime stopped;
	private final Patient patient;
	private int maxPainLevel;
	private Boolean whileAsleep;
	private String comments;

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
		return true;
	}

	@Override
	public String toString() {
		return "Attack [patient=" + patient + ", started=" + started + "]";
	}

}
