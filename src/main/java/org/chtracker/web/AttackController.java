package org.chtracker.web;

import org.chtracker.dao.report.Attack;
import org.chtracker.dao.report.AttackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/attacks")
public class AttackController {
    private final AttackRepository attackRepository;

    @Autowired
    public AttackController(AttackRepository attackRepository) {
        this.attackRepository = attackRepository;
    }

    @GetMapping
    public Iterable<Attack> getAttacks(
            @PageableDefault(sort = "started", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        // TODO: add filterBy patient
        if (from == null && to == null) {
            return attackRepository.findAll(pageable);
        } else if (from == null) {
            return attackRepository.findAllByStartedLessThanEqual(to, pageable);
        } else if (to == null) {
            return attackRepository.findAllByStartedGreaterThanEqual(from, pageable);
        } else {
            return attackRepository.findAllByStartedBetween(from, to, pageable);
        }
    }
}
