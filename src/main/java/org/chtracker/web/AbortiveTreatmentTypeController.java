package org.chtracker.web;

import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.chtracker.dao.metadata.AbortiveTreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/abortive-treatment-types")
public class AbortiveTreatmentTypeController {

	private final AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository;

	@Autowired
	public AbortiveTreatmentTypeController(AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository) {
		this.abortiveTreatmentTypeRepository = abortiveTreatmentTypeRepository;
	}

	@GetMapping("/")
	public Iterable<AbortiveTreatmentType> findAll() {
		return abortiveTreatmentTypeRepository.findAll(Sort.by(new Sort.Order(Direction.ASC, "name")));
	}

}
