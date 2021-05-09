package org.chtracker.web;

import org.chtracker.dao.metadata.PreventiveTreatmentType;
import org.chtracker.dao.metadata.PreventiveTreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preventive-treatment-types")
public class PreventiveTreatmentTypeController {

    private final PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository;

    @Autowired
    public PreventiveTreatmentTypeController(PreventiveTreatmentTypeRepository preventiveTreatmentTypeRepository) {
        this.preventiveTreatmentTypeRepository = preventiveTreatmentTypeRepository;
    }

    @GetMapping("/")
    public Iterable<PreventiveTreatmentType> findAll() {
        return preventiveTreatmentTypeRepository.findAll(Sort.by(new Sort.Order(Direction.ASC, "name")));
    }

}
