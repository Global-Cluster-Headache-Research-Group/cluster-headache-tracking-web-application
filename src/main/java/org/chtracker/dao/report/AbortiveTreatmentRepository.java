package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.Optional;

import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AbortiveTreatmentRepository extends PagingAndSortingRepository<AbortiveTreatment, Integer> {

    Optional<AbortiveTreatment> findByAttackAndStartedAndAbortiveTreatmentType(Attack attack, LocalDateTime started, AbortiveTreatmentType abortiveTreatmentType);

}
