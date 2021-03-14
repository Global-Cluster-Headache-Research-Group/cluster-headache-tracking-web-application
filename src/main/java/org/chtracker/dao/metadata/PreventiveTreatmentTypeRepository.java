package org.chtracker.dao.metadata;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PreventiveTreatmentTypeRepository extends PagingAndSortingRepository<PreventiveTreatmentType, Integer> {

	@Cacheable("preventiveTreatmentType")
	PreventiveTreatmentType findByNameContainingIgnoreCase(String name);
	
}
