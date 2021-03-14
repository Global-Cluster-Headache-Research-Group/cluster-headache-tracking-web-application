package org.chtracker.web;

import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.chtracker.dao.metadata.AbortiveTreatmentTypeRepository;
import org.chtracker.dao.metadata.PreventiveTreatmentType;
import org.chtracker.dao.metadata.PreventiveTreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/TreatmentType")
public class TreatmentTypeController {

	private final AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository;
	private final PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository;

	@Autowired
	public TreatmentTypeController(AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository, PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository,
			PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository2) {
		this.abortiveTreatmentTypeRepository = abortiveTreatmentTypeRepository;
		this.preventiveTreatmentTypeRepository = preventiveTreatmentTypeRepository2;
	}

	@GetMapping("abortive")
	public Iterable<AbortiveTreatmentType> getAbortiveTreatmentTypes() {
		return abortiveTreatmentTypeRepository.findAll(Sort.by(new Sort.Order(Direction.ASC, "name")));
	}

	@GetMapping("preventive")
	public Iterable<PreventiveTreatmentType> getPrevetiveTreatmentTypes() {
		return preventiveTreatmentTypeRepository.findAll(Sort.by(new Sort.Order(Direction.ASC, "name")));
	}

}
