package org.chtracker.dao.report.repositories;

import org.chtracker.dao.metadata.PreventiveTreatmentType;
import org.chtracker.dao.profile.Patient;
import org.chtracker.dao.report.entities.PreventiveTreatment;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PreventiveTreatmentRepository extends PagingAndSortingRepository<PreventiveTreatment, Integer> {
	
	Optional<PreventiveTreatment> findFirstByPatientAndPreventiveTreatmentTypeAndStartedLessThanEqual(Patient patient, PreventiveTreatmentType preventiveTreatmentType, LocalDateTime started);


	
}
