package com.oasis.homehealth.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitNoteDTO {
    private Long id;
    
    // Relationships
    private Long taskId;
    private String taskNumber;
    private Long patientId;
    private String patientName;
    private Long episodeId;
    private String episodeNumber;
    private Long clinicianId;
    private String clinicianName;
    private Long organizationId;
    private String organizationName;
    
    // Visit Information
    private String visitType; // RN_VISIT, PT_VISIT, OT_VISIT, ST_VISIT, HHA_VISIT
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
    
    // Status and Workflow
    private String status; // DRAFT, SUBMITTED, APPROVED, REJECTED, RETURNED
    private Long submittedById;
    private String submittedByName;
    private LocalDateTime submittedAt;
    private Long reviewedById;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private String qaComments;
    private LocalDateTime returnedForCorrectionAt;
    private String correctionComments;
    private Integer revisionNumber;
    
    // Additional Information
    private String specialNotes;
    private Boolean physicianContacted;
    private String physicianContactReason;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByName;
    private String updatedByName;
}

