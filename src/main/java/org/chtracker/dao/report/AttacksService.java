package org.chtracker.dao.report;

import javassist.NotFoundException;
import org.chtracker.dao.profile.Patient;
import org.chtracker.dao.profile.PatientRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AttacksService {
    private final AttackRepository attackRepository;
    private final PatientRepository patientRepository;

    public AttacksService(AttackRepository attackRepository, PatientRepository patientRepository) {
        this.attackRepository = attackRepository;
        this.patientRepository = patientRepository;
    }

    public Iterable<Attack> getAttacks(Pageable pageable, LocalDateTime from, LocalDateTime to) {
        // TODO: add filterBy patient from security context
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

    public void addAttack(LocalDateTime started, LocalDateTime stopped, int patientId, int maxPainLevel, Boolean whileAsleep, String comments) throws NotFoundException {
        Optional<Patient> patient = patientRepository.findById(patientId);
        if (patient.isEmpty()) throw new NotFoundException(String.format("Patient with %n not found", patientId));
        var attack = new Attack(started, stopped, patient.get(), maxPainLevel, whileAsleep, comments);
        attackRepository.save(attack);
    }
}
