package org.chtracker.service.report.dtos;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AddReportDto {
    @NotNull
    @Past
    public LocalDateTime started;

    @NotNull
    @Past
    public LocalDateTime stopped;

    @Min(1)
    @Max(10)
    public int maxPainLevel;

    public Boolean whileAsleep;

    @Size(max = 1000)
    public String comments;

    @NotNull
    public Integer patientId;

    @NotNull
    public ArrayList<UsedAbortiveTreatmentDto> usedTreatments;

    public static class UsedAbortiveTreatmentDto {
        @NotNull
        public int id;

        @NotNull
        @Min(0)
        public int doze;

        @NotNull
        public Boolean successful;
    }
}
