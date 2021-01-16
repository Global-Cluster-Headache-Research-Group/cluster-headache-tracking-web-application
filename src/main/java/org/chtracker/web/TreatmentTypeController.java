package org.chtracker.web;

import java.util.List;

import org.chtracker.dao.metadata.TreatmentType;
import org.chtracker.dao.metadata.TreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/TreatmentType")
public class TreatmentTypeController {

	private final TreatmentTypeRepository repository;

	@Autowired
	public TreatmentTypeController(TreatmentTypeRepository repository) {
		this.repository = repository;
	}

	@GetMapping("abortive")
	public List<TreatmentType> getAbortiveTreatmentTypes() {
		return repository.findByIsAbortiveOrderByName(true);
	}
	
	@GetMapping("preventive")
	public List<TreatmentType> getPrevetiveTreatmentTypes() {
		return repository.findByIsPreventiveOrderByName(true);
	}

}
