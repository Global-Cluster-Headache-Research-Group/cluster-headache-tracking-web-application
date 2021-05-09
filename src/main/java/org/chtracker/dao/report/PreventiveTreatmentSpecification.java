package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class PreventiveTreatmentSpecification implements Specification<PreventiveTreatment> {

    private static final long serialVersionUID = 1L;
    private final int patientId;
    private final LocalDateTime started;
    private final LocalDateTime stopped;
    private final Integer minDoze;
    private final Integer maxDoze;
    private final Integer preventiveTreatmentTypeId;

    public PreventiveTreatmentSpecification(int patientId, LocalDateTime started, LocalDateTime stopped, Integer minDoze, Integer maxDoze, Integer preventiveTreatmentTypeId) {
        this.patientId = patientId;
        this.started = started;
        this.stopped = stopped;
        this.minDoze = minDoze;
        this.maxDoze = maxDoze;
        this.preventiveTreatmentTypeId = preventiveTreatmentTypeId;
    }

    @Override
    public Predicate toPredicate(Root<PreventiveTreatment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("patient").get("id"), patientId));
        if (started != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("started"), started));
        }
        if (stopped != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stopped"), stopped));
        }
        if (minDoze != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("doze"), minDoze));
        }
        if (maxDoze != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("doze"), maxDoze));
        }

        if (preventiveTreatmentTypeId != null) {
            predicates.add(criteriaBuilder.equal(root.join("preventiveTreatmentType").get("id"), preventiveTreatmentTypeId));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    @Override
    public String toString() {
        return "PreventiveTreatmentSpecification [patientId=" + patientId + ", started=" + started + ", stopped=" + stopped + ", minDoze=" + minDoze + ", maxDoze=" + maxDoze
                + ", preventiveTreatmentTypeId=" + preventiveTreatmentTypeId + "]";
    }

}
