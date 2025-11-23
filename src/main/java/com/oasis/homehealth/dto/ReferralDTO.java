package com.oasis.homehealth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralDTO {

    private Long id;
    
    // Referral Information
    private String referralNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate referralDate;
    
    private String referralSource;
    private String referralSourceContact;
    private String referralSourcePhone;
    private String referralSourceEmail;
    private String referralSourceFax;
    private String referralSourceAddress;
    private String referralSourceCity;
    private String referralSourceState;
    private String referralSourceZip;
    
    // Patient Demographics
    private String patientFirstName;
    private String patientMiddleName;
    private String patientLastName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate patientDateOfBirth;
    
    private String patientGender;
    private String patientSsn;
    private String patientMaritalStatus;
    private String patientRace;
    private String patientEthnicity;
    private String patientLanguage;
    
    // Patient Contact
    private String patientPhoneNumber;
    private String patientMobileNumber;
    private String patientEmail;
    
    // Patient Address
    private String patientAddressLine1;
    private String patientAddressLine2;
    private String patientCity;
    private String patientState;
    private String patientZipCode;
    private String patientCounty;
    
    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;
    
    // Insurance/Payer Information
    private String primaryInsurance;
    private String primaryInsuranceId;
    private String primaryInsuranceGroup;
    private String primaryInsurancePhone;
    private String secondaryInsurance;
    private String secondaryInsuranceId;
    private String secondaryInsuranceGroup;
    private String medicaidNumber;
    private String medicareNumber;
    
    // Physician Information
    private String primaryPhysicianName;
    private String primaryPhysicianNpi;
    private String primaryPhysicianPhone;
    private String primaryPhysicianFax;
    private String primaryPhysicianAddress;
    private String primaryPhysicianCity;
    private String primaryPhysicianState;
    private String primaryPhysicianZip;
    private String referringPhysicianName;
    private String referringPhysicianNpi;
    private String referringPhysicianPhone;
    
    // Diagnosis Information
    private String primaryDiagnosis;
    private String primaryDiagnosisIcd10;
    private String primaryDiagnosisDescription;
    private String secondaryDiagnosis1;
    private String secondaryDiagnosis1Icd10;
    private String secondaryDiagnosis2;
    private String secondaryDiagnosis2Icd10;
    private String secondaryDiagnosis3;
    private String secondaryDiagnosis3Icd10;
    
    // Service Information
    private String serviceRequested;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate serviceStartDate;
    
    private String expectedFrequency;
    private String expectedDuration;
    private String specialInstructions;
    
    // Clinical Information
    private String allergies;
    private String currentMedications;
    private String medicalHistory;
    private String functionalLimitations;
    private String equipmentNeeds;
    
    // Status
    private String status;
    private String notes;
    
    // Metadata
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private Long patientId; // If admitted, link to patient
}

