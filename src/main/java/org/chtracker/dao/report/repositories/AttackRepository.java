package org.chtracker.dao.report.repositories;

import org.chtracker.dao.profile.Patient;
import org.chtracker.dao.report.entities.Attack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;

public interface AttackRepository extends PagingAndSortingRepository<Attack, Integer> {

	int countByPatient(Patient patient);

	Page<Attack> findAllByStartedBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

	Page<Attack> findAllByStartedLessThanEqual(LocalDateTime to, Pageable pageable);

	Page<Attack> findAllByStartedGreaterThanEqual(LocalDateTime from, Pageable pageable);
	Iterable<Attack> findAllByPatient(Patient patient);
	Iterable<Attack> findAllByPatientId(int patientId);
}
