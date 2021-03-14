package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chtracker.dao.DataConfiguration;
import org.chtracker.dao.metadata.PreventiveTreatmentType;
import org.chtracker.dao.profile.Patient;

@Entity
@Table(schema = DataConfiguration.REPORT_SCHEMA_NAME)
public class PreventiveTreatment extends AbstractTreatment {

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "preventive_treatment__preventive_treatment_fk"))
	private PreventiveTreatmentType preventiveTreatmentType;

	PreventiveTreatment() {
	}

	public PreventiveTreatment(Patient patient, LocalDateTime started, LocalDateTime stopped, PreventiveTreatmentType preventiveTreatmentType, int doze, String comments) {
		this.setStarted(started);
		this.setPatient(patient);
		this.setPrevetiveTreatmentType(preventiveTreatmentType);
		this.setDoze(doze);
		this.setComments(comments);
	}

	public PreventiveTreatmentType getPrevetiveTreatmentType() {
		return preventiveTreatmentType;
	}

	public void setPrevetiveTreatmentType(PreventiveTreatmentType preventiveTreatmentType) {
		this.preventiveTreatmentType = preventiveTreatmentType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(preventiveTreatmentType);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof PreventiveTreatment))
			return false;
		PreventiveTreatment other = (PreventiveTreatment) obj;
		return Objects.equals(preventiveTreatmentType, other.preventiveTreatmentType);
	}

}
