package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOfCareGoalRequest {
    private String goalCategory;
    private String goalType;
    @NotBlank
    private String goalDescription;
    private String measurableOutcome;
    private String baselineMeasure;
    private String targetMeasure;
    private LocalDate targetDate;
    private LocalDate reviewDate;
    private String status;
    private Integer progressPercentage;
    private String progressNotes;
    private String responsibleDiscipline;
    private String priority;
    private Boolean isActive;
    private String barriers;
    private String interventionsRelated;
    private String notes;
}

