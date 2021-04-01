package org.chtracker.web;

import javassist.NotFoundException;
import org.chtracker.dao.report.AttacksService;
import org.chtracker.web.dto.AddAttackDto;
import org.chtracker.web.dto.ReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/reports")
public class ReportsController {
    private final AttacksService attacksService;

    public ReportsController(AttacksService attacksService) {
        this.attacksService = attacksService;
    }

    @PostMapping
    public void reportAttack(@Valid @RequestBody AddAttackDto dto) throws NotFoundException {
        this.attacksService.addAttack(dto.started, dto.stopped, dto.patientId, dto.maxPainLevel, dto.whileAsleep, dto.comments);
    }

    @GetMapping
    public Page<ReportDto> getAttackReports(
            @PageableDefault(sort = "started", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return this.attacksService.getAttacks(pageable, from, to).map(ReportDto::new);
    }
}
