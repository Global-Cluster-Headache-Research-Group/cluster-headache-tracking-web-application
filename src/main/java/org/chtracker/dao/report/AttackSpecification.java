package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class AttackSpecification implements Specification<Attack> {

    private static final long serialVersionUID = 1L;
    private int patientId;
    private LocalDateTime started;
    private LocalDateTime stopped;
    private Integer minPainLevel;
    private Integer maxPainLevel;
    private Boolean whileAsleep;
    private Integer abortiveTreatmentTypeId;

    public AttackSpecification(int patientId, LocalDateTime started, LocalDateTime stopped, Integer minPainLevel, Integer maxPainLevel, Boolean whileAsleep, Integer abortiveTreatmentTypeId) {
        this.patientId = patientId;
        this.started = started;
        this.stopped = stopped;
        this.minPainLevel = minPainLevel;
        this.maxPainLevel = maxPainLevel;
        this.whileAsleep = whileAsleep;
        this.abortiveTreatmentTypeId = abortiveTreatmentTypeId;
    }

    @Override
    public Predicate toPredicate(Root<Attack> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("patient").get("id"), patientId));
        if (started != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("started"), started));
        }
        if (stopped != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stopped"), stopped));
        }
        if (minPainLevel != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxPainLevel"), minPainLevel));
        }
        if (maxPainLevel != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("maxPainLevel"), maxPainLevel));
        }
        if (whileAsleep != null) {
            predicates.add(criteriaBuilder.equal(root.get("whileAsleep"), whileAsleep));
        }
        if (abortiveTreatmentTypeId != null) {
            predicates.add(criteriaBuilder.equal(root.join("abortiveTreatments").get("abortiveTreatmentType").get("id"), abortiveTreatmentTypeId));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    @Override
    public String toString() {
        return "AttackSpecification [patientId=" + patientId + ", started=" + started + ", stopped=" + stopped + ", minPainLevel=" + minPainLevel + ", maxPainLevel=" + maxPainLevel
                + ", whileAsleep=" + whileAsleep + ", abortiveTreatmentType=" + abortiveTreatmentTypeId + "]";
    }

}
