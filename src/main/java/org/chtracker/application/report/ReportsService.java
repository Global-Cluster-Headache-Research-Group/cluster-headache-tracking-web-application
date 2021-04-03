package org.chtracker.application.report;

import javassist.NotFoundException;
import org.chtracker.application.report.dtos.AddReportDto;
import org.chtracker.application.report.dtos.ReportDto;
import org.chtracker.dao.metadata.AbortiveTreatmentType;
import org.chtracker.dao.metadata.AbortiveTreatmentTypeRepository;
import org.chtracker.dao.profile.Patient;
import org.chtracker.dao.profile.PatientRepository;
import org.chtracker.dao.report.entities.AbortiveTreatment;
import org.chtracker.dao.report.entities.Attack;
import org.chtracker.dao.report.repositories.AbortiveTreatmentRepository;
import org.chtracker.dao.report.repositories.AttackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReportsService {
    private final AttackRepository attackRepository;
    private final PatientRepository patientRepository;
    private final AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository;
    private final AbortiveTreatmentRepository abortiveTreatmentRepository;

    public ReportsService(AttackRepository attackRepository, PatientRepository patientRepository, AbortiveTreatmentTypeRepository abortiveTreatmentTypeRepository, AbortiveTreatmentRepository abortiveTreatmentRepository) {
        this.attackRepository = attackRepository;
        this.patientRepository = patientRepository;
        this.abortiveTreatmentTypeRepository = abortiveTreatmentTypeRepository;
        this.abortiveTreatmentRepository = abortiveTreatmentRepository;
    }

    public Page<ReportDto> getReports(Pageable pageable, LocalDateTime from, LocalDateTime to) {
        // TODO: add filterBy patient from security context
        if (from == null && to == null) {
            return attackRepository.findAll(pageable).map(ReportDto::new);
        } else if (from == null) {
            return attackRepository.findAllByStartedLessThanEqual(to, pageable).map(ReportDto::new);
        } else if (to == null) {
            return attackRepository.findAllByStartedGreaterThanEqual(from, pageable).map(ReportDto::new);
        } else {
            return attackRepository.findAllByStartedBetween(from, to, pageable).map(ReportDto::new);
        }
    }

    @Transactional
    public void addReport(AddReportDto dto) throws NotFoundException {
        Optional<Patient> patient = patientRepository.findById(dto.patientId);
        if (patient.isEmpty()) throw new NotFoundException(String.format("Patient with %d not found", dto.patientId));

        var attack = new Attack(dto.started, dto.stopped, patient.get(), dto.maxPainLevel, dto.whileAsleep, dto.comments);
        attackRepository.save(attack);

        for (AddReportDto.UsedAbortiveTreatmentDto usedTreatment: dto.usedTreatments) {
            Optional<AbortiveTreatmentType> type = abortiveTreatmentTypeRepository.findById(usedTreatment.id);
            if (type.isEmpty()) throw new NotFoundException(String.format("Abortive type with %d not found", usedTreatment.id));
            abortiveTreatmentRepository.save(new AbortiveTreatment(patient.get(), attack, type.get(), usedTreatment.doze, usedTreatment.successful));
        }
    }
}
