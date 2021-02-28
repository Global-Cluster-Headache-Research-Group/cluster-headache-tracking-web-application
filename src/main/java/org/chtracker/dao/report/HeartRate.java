package org.chtracker.dao.report;

import java.time.LocalDateTime;

import org.chtracker.dao.profile.Patient;

public class HeartRate {
	
	private final LocalDateTime localDateTime;
	private final int bpm;
	private final Patient patient;
	
	public HeartRate(LocalDateTime localDateTime, int bpm, Patient patient) {
		this.localDateTime = localDateTime;
		this.bpm = bpm;
		this.patient = patient;
	}
	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}
	public int getBpm() {
		return bpm;
	}
	public Patient getPatient() {
		return patient;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bpm;
		result = prime * result + ((localDateTime == null) ? 0 : localDateTime.hashCode());
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
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
		HeartRate other = (HeartRate) obj;
		if (bpm != other.bpm)
			return false;
		if (localDateTime == null) {
			if (other.localDateTime != null)
				return false;
		} else if (!localDateTime.equals(other.localDateTime))
			return false;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "HeartRate [patient=" + patient + ", localDateTime=" + localDateTime + ", bpm=" + bpm + "]";
	}
	

}
