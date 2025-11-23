package com.oasis.homehealth.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOfCareGoalDTO {
    private Long id;
    private String goalCategory;
    private String goalType;
    private String goalDescription;
    private String measurableOutcome;
    private String baselineMeasure;
    private String targetMeasure;
    private LocalDate targetDate;
    private LocalDate reviewDate;
    private String status;
    private Integer progressPercentage;
    private String progressNotes;
    private LocalDate achievedDate;
    private String responsibleDiscipline;
    private String priority;
    private Boolean isActive;
    private String barriers;
    private String interventionsRelated;
    private String notes;
    private Boolean isOverdue;
    private Integer daysUntilTarget;
}

