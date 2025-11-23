package com.oasis.homehealth.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOfCareFrequencyDTO {
    private Long id;
    private String disciplineType;
    private String disciplineDescription;
    private String frequencyCode;
    private Integer visitsPerWeek;
    private Integer numberOfWeeks;
    private Integer totalVisits;
    private Integer completedVisits;
    private Integer estimatedMinutesPerVisit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String status;
    private String notes;
    private Integer remainingVisits;
    private Double completionPercentage;
}

