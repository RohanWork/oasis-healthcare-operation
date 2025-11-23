package com.oasis.homehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOfCareDTO {
    private Long id;
    private String pocNumber;
    
    // Related entities
    private Long patientId;
    private String patientName;
    private Long episodeId;
    private String episodeNumber;
    private Long oasisAssessmentId;
    private Long organizationId;
    
    // Dates
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer certificationPeriodDays;
    
    // Diagnosis
    private String primaryDiagnosisCode;
    private String primaryDiagnosisDescription;
    private String secondaryDiagnosisCode;
    private String secondaryDiagnosisDescription;
    private String otherDiagnoses;
    
    // Clinical info
    private String functionalLimitations;
    private String safetyMeasures;
    private String nutritionalRequirements;
    private String medicationList;
    private Boolean medicationManagementNeeded;
    
    // Status
    private String status;
    private String statusReason;
    
    // Physician
    private String physicianName;
    private String physicianPhone;
    private Boolean physicianSignatureRequired;
    private LocalDate physicianSignedDate;
    private Boolean physicianSignatureObtained;
    
    // Workflow
    private Long createdByClinicianId;
    private String createdByClinicianName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private Long rejectedById;
    private String rejectedByName;
    private LocalDateTime rejectedAt;
    private String rejectionReason;
    
    // Additional
    private String specialInstructions;
    private String dmeEquipment;
    private String notes;
    
    // Child collections
    private List<PlanOfCareFrequencyDTO> frequencies;
    private List<PlanOfCareInterventionDTO> interventions;
    private List<PlanOfCareGoalDTO> goals;
    private List<PhysicianOrderDTO> physicianOrders;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // Calculated fields
    private Integer remainingDays;
    private Boolean isExpiringSoon;
    private Boolean isExpired;
}

