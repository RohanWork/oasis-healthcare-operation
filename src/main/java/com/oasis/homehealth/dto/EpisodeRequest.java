package com.oasis.homehealth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodeRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Episode type is required")
    @Pattern(regexp = "ADMISSION|RECERTIFICATION|RESUMPTION_OF_CARE", 
             message = "Episode type must be ADMISSION, RECERTIFICATION, or RESUMPTION_OF_CARE")
    private String episodeType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Certification start date is required")
    private LocalDate certificationStartDate;

    @NotNull(message = "Certification end date is required")
    @Future(message = "Certification end date must be in the future")
    private LocalDate certificationEndDate;

    @NotNull(message = "Certification period is required")
    @Min(value = 1, message = "Certification period must be at least 1 day")
    @Max(value = 60, message = "Certification period cannot exceed 60 days")
    private Integer certificationPeriod;

    // Diagnosis
    @NotBlank(message = "Primary diagnosis code is required")
    @Size(max = 20, message = "Primary diagnosis code must not exceed 20 characters")
    private String primaryDiagnosisCode;

    @NotBlank(message = "Primary diagnosis description is required")
    @Size(max = 500, message = "Primary diagnosis description must not exceed 500 characters")
    private String primaryDiagnosisDescription;

    private String secondaryDiagnosisCodes;
    private String secondaryDiagnosisDescriptions;
    private String admittingDiagnosis;
    private String surgicalProcedures;
    private String treatmentAuthorizationCode;

    // Physician
    @NotBlank(message = "Physician name is required")
    private String physicianName;

    private String physicianNpi;
    private String physicianPhone;
    private LocalDate physicianOrdersDate;

    // Care Team
    private Long caseManagerId;
    private Long primaryNurseId;
    private Long primaryTherapistId;

    // Status
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|PENDING|COMPLETED|CANCELLED|ON_HOLD", 
             message = "Status must be ACTIVE, PENDING, COMPLETED, CANCELLED, or ON_HOLD")
    private String status;

    private String statusReason;

    // Insurance
    private Long primaryInsuranceId;

    // Financial
    private String expectedFrequency;
    private Integer totalAuthorizedVisits;

    // Admission Source
    private String admissionSource;
    private String admissionSourceFacility;

    // Notes
    private String clinicalNotes;
    private String specialInstructions;

    // Flags
    private Boolean isHomebound;
    private Boolean requiresSkilledNursing;
    private Boolean requiresTherapy;
    private Boolean requiresAide;
}

