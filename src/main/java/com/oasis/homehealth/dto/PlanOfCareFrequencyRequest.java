package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOfCareFrequencyRequest {
    @NotNull
    private String disciplineType;
    private String disciplineDescription;
    private String frequencyCode;
    private Integer visitsPerWeek;
    private Integer numberOfWeeks;
    private Integer totalVisits;
    private Integer estimatedMinutesPerVisit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String status;
    private String notes;
}

