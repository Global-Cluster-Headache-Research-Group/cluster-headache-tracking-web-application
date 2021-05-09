package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.chtracker.dao.DataConfiguration;
import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(schema = DataConfiguration.REPORT_SCHEMA_NAME, uniqueConstraints = {
        @UniqueConstraint(name = "abortive_uniq", columnNames = { "attack_id", "started", "abortive_treatment_type_id" })
}, indexes = {
        @Index(name = "abortive_treatment__attack_id_idx", columnList = "attack_id"),
        @Index(name = "abortive_treatment__abortive_treatment_type_idx", columnList = "abortive_treatment_type_id")
})
public class AbortiveTreatment extends AbstractTreatment {

    @Id
    @GeneratedValue(generator = "abortiveTreatmentSequenceGenerator", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "abortiveTreatmentSequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "report.abortive_treatment_seq"),
            @Parameter(name = "initial_value", value = "1"),
    })
    private int id;

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "abortive_treatment__attack_fk"))
    private Attack attack;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "abortive_treatment__abortive_treatment_fk"))
    private AbortiveTreatmentType abortiveTreatmentType;

    private Boolean successful;

    AbortiveTreatment() {
    }

    public AbortiveTreatment(Attack attack, LocalDateTime started, LocalDateTime stopped, AbortiveTreatmentType abortiveTreatmentType, int doze, Boolean successful) {
        this.attack = attack;
        this.successful = successful;
        this.setStarted(started);
        this.setStopped(stopped);
        this.setAbortiveTreatmentType(abortiveTreatmentType);
        this.setDoze(doze);
    }

    @Override
    public int getId() {
        return id;
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

    @Override
    public String toString() {
        return "AbortiveTreatment [id=" + id + ", attack=" + attack + ", abortiveTreatmentType=" + abortiveTreatmentType + ", successful=" + successful + "]";
    }

}
