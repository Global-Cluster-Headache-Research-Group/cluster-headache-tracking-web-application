package org.chtracker.web;

import org.chtracker.dao.report.Attack;
import org.chtracker.dao.report.AttacksService;
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
    private final AttacksService attacksService;

    public AttackController(AttacksService attacksService) {
        this.attacksService = attacksService;
    }

    @GetMapping
    public Iterable<Attack> getAttacks(
            @PageableDefault(sort = "started", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return this.attacksService.getAttacks(pageable, from, to);
    }
}
