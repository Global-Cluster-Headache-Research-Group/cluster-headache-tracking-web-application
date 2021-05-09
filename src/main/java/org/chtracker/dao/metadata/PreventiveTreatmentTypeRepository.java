package org.chtracker.dao.metadata;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PreventiveTreatmentTypeRepository extends PagingAndSortingRepository<PreventiveTreatmentType, Integer> {

    @Cacheable("preventiveTreatmentType")
    Optional<PreventiveTreatmentType> findByNameContainingIgnoreCase(String name);

    PreventiveTreatmentType findByName(String name);

}
