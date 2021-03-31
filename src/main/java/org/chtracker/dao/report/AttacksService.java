package org.chtracker.dao.report;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AttacksService {
    private final AttackRepository attackRepository;

    public AttacksService(AttackRepository attackRepository) {
        this.attackRepository = attackRepository;
    }

    public Iterable<Attack> getAttacks(Pageable pageable, LocalDateTime from, LocalDateTime to) {
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
