package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotNull private Long patientId;
    @NotNull private Long episodeId;
    private Long planOfCareId;
    private Long assignedToId;
    
    @NotNull private String taskType;
    @NotNull private String title;
    private String description;
    
    @NotNull private LocalDate scheduledDate;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private Integer estimatedDurationMinutes;
    
    private String status;
    private String priority;
    private Boolean isUrgent;
    
    private String visitLocation;
    private String specialInstructions;
    private String patientAvailability;
    private String notes;
    
    private Boolean isBillable;
    private String billingCode;
}
