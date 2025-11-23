package com.oasis.homehealth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOfCareInterventionDTO {
    private Long id;
    private String interventionCategory;
    private String interventionCode;
    private String interventionDescription;
    private String frequency;
    private String duration;
    private String responsibleDiscipline;
    private String collaborativeDisciplines;
    private String priority;
    private String status;
    private Boolean isActive;
    private String expectedOutcome;
    private String specialInstructions;
    private String contraindications;
    private String notes;
}

