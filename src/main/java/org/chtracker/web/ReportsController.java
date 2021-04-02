package org.chtracker.web;

import javassist.NotFoundException;
import org.chtracker.application.report.ReportsService;
import org.chtracker.application.report.dtos.AddReportDto;
import org.chtracker.application.report.dtos.ReportDto;
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
    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @PostMapping
    public void reportAttack(@Valid @RequestBody AddReportDto dto) throws NotFoundException {
        this.reportsService.addReport(dto);
    }

    @GetMapping
    public Page<ReportDto> getAttackReports(
            @PageableDefault(sort = "started", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return this.reportsService.getReports(pageable, from, to);
    }
}
