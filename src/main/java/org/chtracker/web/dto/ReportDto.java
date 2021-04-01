package org.chtracker.web.dto;

import org.chtracker.dao.report.AbortiveTreatment;
import org.chtracker.dao.report.Attack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReportDto {
    public final int attackId;
    public final LocalDateTime started;
    public final LocalDateTime stopped;
    public final int maxPainLevel;
    public final String comments;
    public final List<AbortiveTreatmentDto> usedAbortiveTreatments;

    public ReportDto(Attack attack) {
        this.attackId = attack.getId();
        this.started = attack.getStarted();
        this.stopped = attack.getStopped();
        this.maxPainLevel = attack.getMaxPainLevel();
        this.comments = attack.getComments();
        this.usedAbortiveTreatments = attack.getAbortiveTreatments()
                .stream()
                .map(AbortiveTreatmentDto::new)
                .collect(Collectors.toList());
    }

    static class AbortiveTreatmentDto {
        public final String name;
        public final String tradeName;
        public final String units;
        public final Boolean successful;
        public final int doze;

        AbortiveTreatmentDto(AbortiveTreatment treatment) {
            var abortiveTreatmentType = treatment.getAbortiveTreatmentType();
            this.name = abortiveTreatmentType.getName();
            this.units = abortiveTreatmentType.getUnits();
            this.tradeName = abortiveTreatmentType.getTradeName();
            this.successful = treatment.getSuccessful();
            this.doze = treatment.getDoze();
        }
    }
}