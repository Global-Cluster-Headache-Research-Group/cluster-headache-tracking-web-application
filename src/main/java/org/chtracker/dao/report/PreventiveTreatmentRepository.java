package org.chtracker.dao.report;

import java.time.LocalDateTime;
import java.util.Optional;

import org.chtracker.dao.metadata.PreventiveTreatmentType;
import org.chtracker.dao.profile.Patient;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PreventiveTreatmentRepository extends PagingAndSortingRepository<PreventiveTreatment, Integer>, JpaSpecificationExecutor<PreventiveTreatment> {

    Optional<PreventiveTreatment> findFirstByPatientAndPreventiveTreatmentTypeAndStartedLessThanOrderByStartedDesc(Patient patient, PreventiveTreatmentType preventiveTreatmentType,
            LocalDateTime started);

}
