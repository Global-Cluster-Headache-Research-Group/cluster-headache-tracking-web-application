package org.chtracker.dao.report;

import org.chtracker.dao.profile.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;

public interface AttackRepository extends PagingAndSortingRepository<Attack, Integer> {

	int countByPatient(Patient patient);

	Page<Attack> findAllByStartedBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

	Page<Attack> findAllByStartedLessThanEqual(LocalDateTime to, Pageable pageable);

	Page<Attack> findAllByStartedGreaterThanEqual(LocalDateTime from, Pageable pageable);
}
