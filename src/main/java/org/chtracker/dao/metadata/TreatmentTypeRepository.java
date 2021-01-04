package org.chtracker.dao.metadata;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TreatmentTypeRepository extends PagingAndSortingRepository<TreatmentType, Integer> {

	@Cacheable("treatment")
	TreatmentType findByNameContainingIgnoreCase(String name);
}
