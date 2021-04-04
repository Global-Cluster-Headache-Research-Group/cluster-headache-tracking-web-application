package org.chtracker.dao.report;

import org.chtracker.dao.profile.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttackRepository extends PagingAndSortingRepository<Attack, Integer>,JpaSpecificationExecutor<Attack>  {

	int countByPatient(Patient patient);
	Iterable<Attack> findAllByPatient(Patient patient);
	Iterable<Attack> findAllByPatientId(int patientId);
//	Page<Attack> findAllByPatientId(int patientId,Pageable pageable);
//	Page<Attack> findAllByPatientId(Specification<Attack> specification, Pageable pageable);
}
