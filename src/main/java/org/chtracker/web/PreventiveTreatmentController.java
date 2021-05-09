package org.chtracker.web;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;

import org.chtracker.dao.report.PreventiveTreatment;
import org.chtracker.dao.report.PreventiveTreatmentRepository;
import org.chtracker.dao.report.PreventiveTreatmentSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preventive-treatment")
public class PreventiveTreatmentController {

    static final int DEFAULT_PAGE_SIZE = 100;
    private final PreventiveTreatmentRepository preventiveTreatmentRepository;

    @Autowired
    public PreventiveTreatmentController(PreventiveTreatmentRepository preventiveTreatmentRepository) {
        this.preventiveTreatmentRepository = preventiveTreatmentRepository;
    }

    @GetMapping()
    public Page<PreventiveTreatment> findAll(
            @RequestParam int patientId,
            @RequestParam(required = false) @Past LocalDate fromDate,
            @RequestParam(required = false) @Past LocalDate toDate,
            @RequestParam(required = false) @Min(1) Integer minDoze,
            @RequestParam(required = false) @Min(1) Integer maxDoze,
            @RequestParam(required = false) Integer preventiveTreatmentTypeId,
            @RequestParam(defaultValue = "0", required = false) @Min(0) int pageNumber,
            @RequestParam(defaultValue = "10", required = false) @Min(10) @Max(1000) int pageSize) {
        PreventiveTreatmentSpecification specification = createSpecification(patientId, fromDate, toDate, minDoze, maxDoze, preventiveTreatmentTypeId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("started").descending());
        return preventiveTreatmentRepository.findAll(specification, pageable);
    }

    @GetMapping("/count")
    public long count(
            @RequestParam int patientId,
            @RequestParam(required = false) @Past LocalDate fromDate,
            @RequestParam(required = false) @Past LocalDate toDate,
            @RequestParam(required = false) @Min(1) Integer minDoze,
            @RequestParam(required = false) @Min(1) Integer maxDoze,
            @RequestParam(required = false) Integer preventiveTreatmentTypeId,
            @RequestParam(defaultValue = "0", required = false) @Min(0) int pageNumber) {
        PreventiveTreatmentSpecification specification = createSpecification(patientId, fromDate, toDate, minDoze, maxDoze, preventiveTreatmentTypeId);
        return preventiveTreatmentRepository.count(specification);
    }

    private PreventiveTreatmentSpecification createSpecification(int patientId, LocalDate fromDate, LocalDate toDate, Integer minDoze, Integer maxDoze, Integer preventiveTreatmentTypeId) {
        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate == null ? null : toDate.plusDays(1).atStartOfDay();
        return new PreventiveTreatmentSpecification(patientId, fromDateTime, toDateTime, minDoze, maxDoze, preventiveTreatmentTypeId);
    }

}
