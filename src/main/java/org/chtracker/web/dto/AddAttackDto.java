package org.chtracker.web.dto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class AddAttackDto {
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
}
