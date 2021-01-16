package org.chtracker.dao.metadata;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TreatmentTypeRepository extends PagingAndSortingRepository<TreatmentType, Integer> {

	@Cacheable("treatment")
	TreatmentType findByNameContainingIgnoreCase(String name);
	
	@Cacheable("treatment")
	List<TreatmentType> findByIsAbortiveOrderByName(boolean isAbortive);
	
	@Cacheable("treatment")
	List<TreatmentType> findByIsPreventiveOrderByName(boolean isPreventive);
}
