package org.chtracker.web;

import org.chtracker.dao.report.Attack;
import org.chtracker.dao.report.AttackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attacks")
public class AttackController {

	private final AttackRepository attackRepository;

	@Autowired
	public AttackController(AttackRepository attackRepository) {
		this.attackRepository = attackRepository;
	}

	/*
	 * @GetMapping("/")
	 * public Iterable<Attack> findAll() {
	 * return attackRepository.findAll();
	 * }
	 */
	
	@GetMapping("/")
	public Iterable<Attack> findAllByPatient(@RequestParam int patientId) {
		return attackRepository.findAllByPatientId(patientId);
	}

}
