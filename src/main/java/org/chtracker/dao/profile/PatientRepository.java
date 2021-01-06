package org.chtracker.dao.profile;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface PatientRepository extends PagingAndSortingRepository<Patient, Integer> {

	Patient findByLogin(String login);
}
