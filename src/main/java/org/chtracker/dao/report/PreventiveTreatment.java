package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chtracker.dao.DataConfiguration;
import org.chtracker.dao.metadata.PreventiveTreatmentType;
import org.chtracker.dao.profile.Patient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(schema = DataConfiguration.REPORT_SCHEMA_NAME, uniqueConstraints = {
        @UniqueConstraint(name = "preventive_uniq", columnNames = { "patient_id", "started", "preventive_treatment_type_id" }) }, indexes = {
                @Index(name = "preventive_treatment__patient_id_started_idx", columnList = "patient_id,started"),
                @Index(name = "preventive_treatment__patient_id_preventive_treatment_type_id_idx", columnList = "patient_id,preventive_treatment_type_id"),
        })
public class PreventiveTreatment extends AbstractTreatment {

    @Id
    @GeneratedValue(generator = "preventiveTreatmentSequenceGenerator", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "preventiveTreatmentSequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "report.preventive_treatment_seq"),
            @Parameter(name = "initial_value", value = "1"),
    })
    private int id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "preventive_treatment__patient_fk"))
    private Patient patient;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "preventive_treatment__preventive_treatment_fk"))
    private PreventiveTreatmentType preventiveTreatmentType;

    PreventiveTreatment() {
    }

    @Override
    public int getId() {
        return id;
    }

    public PreventiveTreatment(Patient patient, LocalDateTime started, LocalDateTime stopped, PreventiveTreatmentType preventiveTreatmentType, int doze, String comments) {
        this.patient = patient;
        this.setStarted(started);
        this.setStopped(stopped);
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
        result = prime * result + Objects.hash(id);
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
        return id == other.id;
    }

    public Patient getPatient() {
        return patient;
    }

}
