package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitNoteRequest {
    @NotNull(message = "Task ID is required")
    private Long taskId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Episode ID is required")
    private Long episodeId;
    
    @NotNull(message = "Visit type is required")
    private String visitType; // RN_VISIT, PT_VISIT, OT_VISIT, ST_VISIT, HHA_VISIT
    
    @NotNull(message = "Visit date is required")
    private LocalDate visitDate;
    
    private LocalTime visitStartTime;
    private LocalTime visitEndTime;
    private Integer visitDurationMinutes;
    
    // Clinical Documentation
    private String chiefComplaint;
    private String vitalSigns; // JSON string
    private String assessmentFindings;
    private String interventionsProvided;
    private String patientResponse;
    private String teachingProvided;
    private String followUpPlan;
    private LocalDate nextVisitDate;
    
    // Travel Information
    private Double mileage;
    private Integer travelTimeMinutes;
    
    // Status
    private String status; // DRAFT, SUBMITTED, APPROVED, REJECTED, RETURNED
    
    // Additional Information
    private String specialNotes;
    private Boolean physicianContacted;
    private String physicianContactReason;
}

