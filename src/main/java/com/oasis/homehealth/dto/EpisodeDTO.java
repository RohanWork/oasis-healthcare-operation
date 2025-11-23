package com.oasis.homehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodeDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long organizationId;
    private String organizationName;
    
    // Episode Information
    private String episodeNumber;
    private String episodeType;
    private String episodeTitle;
    
    // Dates
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate certificationStartDate;
    private LocalDate certificationEndDate;
    private Integer certificationPeriod;
    private Integer remainingDays;
    
    // Diagnosis
    private String primaryDiagnosisCode;
    private String primaryDiagnosisDescription;
    private String secondaryDiagnosisCodes;
    private String secondaryDiagnosisDescriptions;
    private String admittingDiagnosis;
    private String surgicalProcedures;
    private String treatmentAuthorizationCode;
    
    // Physician
    private String physicianName;
    private String physicianNpi;
    private String physicianPhone;
    private LocalDate physicianOrdersDate;
    
    // Care Team
    private Long caseManagerId;
    private String caseManagerName;
    private Long primaryNurseId;
    private String primaryNurseName;
    private Long primaryTherapistId;
    private String primaryTherapistName;
    
    // Status
    private String status;
    private String statusReason;
    private Boolean isActive;
    private Boolean isCertificationExpiringSoon;
    
    // Insurance
    private Long primaryInsuranceId;
    private String primaryInsuranceCompany;
    
    // Financial
    private String expectedFrequency;
    private Integer totalAuthorizedVisits;
    private Integer visitsCompleted;
    private Integer remainingVisits;
    
    // Admission Source
    private String admissionSource;
    private String admissionSourceFacility;
    
    // Discharge Information
    private String dischargeReason;
    private String dischargeDisposition;
    
    // Notes
    private String clinicalNotes;
    private String specialInstructions;
    
    // Flags
    private Boolean isHomebound;
    private Boolean requiresSkilledNursing;
    private Boolean requiresTherapy;
    private Boolean requiresAide;
    
    // Metadata
    private LocalDate createdAt;
    private String createdBy;
}

