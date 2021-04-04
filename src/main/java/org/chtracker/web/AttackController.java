package org.chtracker.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.chtracker.dao.report.Attack;
import org.chtracker.dao.report.AttackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attacks")
public class AttackController {

	static final int DEFAULT_PAGE_SIZE = 100;
	private final AttackRepository attackRepository;

	@Autowired
	public AttackController(AttackRepository attackRepository) {
		this.attackRepository = attackRepository;
	}

	@GetMapping()
	public Iterable<Attack> findAllByPatient(
			@RequestParam int patientId, 
			@RequestParam(defaultValue = "0",required = false) @Min(0) int pageNumber,
			@RequestParam(defaultValue = "10",required = false) @Min(10) @Max(1000) int pageSize
			) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("started").descending());
		return attackRepository.findAllByPatientId(patientId, pageable);
	}

}
