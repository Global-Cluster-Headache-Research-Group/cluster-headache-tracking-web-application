package org.chtracker.web;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;

import org.chtracker.dao.report.Attack;
import org.chtracker.dao.report.AttackRepository;
import org.chtracker.dao.report.AttackSpecification;
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
	public Iterable<Attack> findAll(
			@RequestParam int patientId,
			@RequestParam(required = false) @Past LocalDate fromDate,
			@RequestParam(required = false) @Past LocalDate toDate,
			@RequestParam(required = false) @Min(0) @Max(10) Integer minPainLevel,
			@RequestParam(required = false) @Min(0) @Max(10) Integer maxPainLevel,
			@RequestParam(required = false) Boolean whileAsleep,
			@RequestParam(required = false) Integer abortiveTreatmentTypeId,
			@RequestParam(defaultValue = "0", required = false) @Min(0) int pageNumber,
			@RequestParam(defaultValue = "10", required = false) @Min(10) @Max(1000) int pageSize) {
		AttackSpecification specification = createAttackSpecification(patientId, fromDate, toDate, minPainLevel, maxPainLevel, whileAsleep, abortiveTreatmentTypeId);
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("started").descending());
		return attackRepository.findAll(specification, pageable);
	}
	
	@GetMapping("/count")
	public long count(
			@RequestParam int patientId,
			@RequestParam(required = false) @Past LocalDate fromDate,
			@RequestParam(required = false) @Past LocalDate toDate,
			@RequestParam(required = false) @Min(0) @Max(10) Integer minPainLevel,
			@RequestParam(required = false) @Min(0) @Max(10) Integer maxPainLevel,
			@RequestParam(required = false) Boolean whileAsleep,
			@RequestParam(required = false) Integer abortiveTreatmentTypeId) {
		AttackSpecification specification = createAttackSpecification(patientId, fromDate, toDate, minPainLevel, maxPainLevel, whileAsleep, abortiveTreatmentTypeId);
		return attackRepository.count(specification);
	}

	private AttackSpecification createAttackSpecification(int patientId, LocalDate fromDate, LocalDate toDate, Integer minPainLevel, Integer maxPainLevel, Boolean whileAsleep,
			Integer abortiveTreatmentTypeId) {
		LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
		LocalDateTime toDateTime = toDate == null ? null : toDate.plusDays(1).atStartOfDay();
		return new AttackSpecification(patientId, fromDateTime, toDateTime, minPainLevel, maxPainLevel, whileAsleep, abortiveTreatmentTypeId);
	}
	
}
