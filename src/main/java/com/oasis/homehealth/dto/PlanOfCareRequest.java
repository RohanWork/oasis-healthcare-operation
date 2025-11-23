package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanOfCareRequest {
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Episode ID is required")
    private Long episodeId;
    
    private Long oasisAssessmentId; // Optional: link to source OASIS
    
    @NotNull(message = "Start date is required")
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
    
    // Additional
    private String specialInstructions;
    private String dmeEquipment;
    private String notes;
    
    // Child collections
    private List<PlanOfCareFrequencyRequest> frequencies;
    private List<PlanOfCareInterventionRequest> interventions;
    private List<PlanOfCareGoalRequest> goals;
    private List<PhysicianOrderRequest> physicianOrders;
}

