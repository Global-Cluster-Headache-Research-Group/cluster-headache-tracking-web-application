package org.chtracker.dao.report;

import org.chtracker.dao.profile.Patient;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttackRepository extends PagingAndSortingRepository<Attack, Integer> {

	int countByPatient(Patient patient);
}
