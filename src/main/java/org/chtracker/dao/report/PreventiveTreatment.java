package org.chtracker.dao.report;

import java.time.LocalDateTime;

import org.chtracker.dao.metadata.TreatmentType;
import org.chtracker.dao.profile.Patient;

public class PreventiveTreatment {
	private LocalDateTime started;
	private final Patient patient;
	private final TreatmentType treatmentType;
	private int doze;
	private String commnets;

	public PreventiveTreatment(LocalDateTime started, Patient patient, TreatmentType treatmentType, int doze, String commnets) {
		super();
		this.started = started;
		this.patient = patient;
		this.treatmentType = treatmentType;
		this.doze = doze;
		this.commnets = commnets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		PreventiveTreatment other = (PreventiveTreatment) obj;
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

	@Override
	public String toString() {
		return "PreventiveTreatment [patient=" + patient + ", treatmentType=" + treatmentType + ", started=" + started + ", doze=" + doze + "]";
	}

	public LocalDateTime getStarted() {
		return started;
	}

	public void setStarted(LocalDateTime started) {
		this.started = started;
	}

	public int getDoze() {
		return doze;
	}

	public void setDoze(int doze) {
		this.doze = doze;
	}

	public String getCommnets() {
		return commnets;
	}

	public void setCommnets(String commnets) {
		this.commnets = commnets;
	}

	public Patient getPatient() {
		return patient;
	}

	public TreatmentType getTreatmentType() {
		return treatmentType;
	}
	

	
	
}
