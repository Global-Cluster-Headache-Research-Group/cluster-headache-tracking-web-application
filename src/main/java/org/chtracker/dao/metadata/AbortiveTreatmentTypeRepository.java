package org.chtracker.dao.metadata;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AbortiveTreatmentTypeRepository extends PagingAndSortingRepository<AbortiveTreatmentType, Integer> {

	@Cacheable("abortiveTreatmentType")
	AbortiveTreatmentType findByNameContainingIgnoreCase(String name);
	
}
