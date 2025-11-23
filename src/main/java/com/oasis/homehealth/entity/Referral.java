package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "referrals", indexes = {
    @Index(name = "idx_referral_number", columnList = "referral_number"),
    @Index(name = "idx_referral_org", columnList = "organization_id"),
    @Index(name = "idx_referral_status", columnList = "status"),
    @Index(name = "idx_referral_date", columnList = "referral_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referral extends BaseEntity {

    // Organization (Multi-tenant)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // Referral Information
    @Column(name = "referral_number", unique = true, length = 50)
    private String referralNumber;

    @Column(name = "referral_date", nullable = false)
    private LocalDate referralDate;

    @Column(name = "referral_source", length = 200)
    private String referralSource;

    @Column(name = "referral_source_contact", length = 100)
    private String referralSourceContact;

    @Column(name = "referral_source_phone", length = 20)
    private String referralSourcePhone;

    @Column(name = "referral_source_email", length = 100)
    private String referralSourceEmail;

    @Column(name = "referral_source_fax", length = 20)
    private String referralSourceFax;

    @Column(name = "referral_source_address", length = 255)
    private String referralSourceAddress;

    @Column(name = "referral_source_city", length = 100)
    private String referralSourceCity;

    @Column(name = "referral_source_state", length = 2)
    private String referralSourceState;

    @Column(name = "referral_source_zip", length = 10)
    private String referralSourceZip;

    // Patient Demographics
    @Column(name = "patient_first_name", length = 100)
    private String patientFirstName;

    @Column(name = "patient_middle_name", length = 100)
    private String patientMiddleName;

    @Column(name = "patient_last_name", length = 100)
    private String patientLastName;

    @Column(name = "patient_date_of_birth")
    private LocalDate patientDateOfBirth;

    @Column(name = "patient_gender", length = 10)
    private String patientGender;

    @Column(name = "patient_ssn", length = 11)
    private String patientSsn;

    @Column(name = "patient_marital_status", length = 20)
    private String patientMaritalStatus;

    @Column(name = "patient_race", length = 50)
    private String patientRace;

    @Column(name = "patient_ethnicity", length = 50)
    private String patientEthnicity;

    @Column(name = "patient_language", length = 50)
    private String patientLanguage;

    // Patient Contact
    @Column(name = "patient_phone_number", length = 20)
    private String patientPhoneNumber;

    @Column(name = "patient_mobile_number", length = 20)
    private String patientMobileNumber;

    @Column(name = "patient_email", length = 100)
    private String patientEmail;

    // Patient Address
    @Column(name = "patient_address_line1", length = 255)
    private String patientAddressLine1;

    @Column(name = "patient_address_line2", length = 255)
    private String patientAddressLine2;

    @Column(name = "patient_city", length = 100)
    private String patientCity;

    @Column(name = "patient_state", length = 2)
    private String patientState;

    @Column(name = "patient_zip_code", length = 10)
    private String patientZipCode;

    @Column(name = "patient_county", length = 100)
    private String patientCounty;

    // Emergency Contact
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    // Insurance/Payer Information
    @Column(name = "primary_insurance", length = 100)
    private String primaryInsurance;

    @Column(name = "primary_insurance_id", length = 50)
    private String primaryInsuranceId;

    @Column(name = "primary_insurance_group", length = 50)
    private String primaryInsuranceGroup;

    @Column(name = "primary_insurance_phone", length = 20)
    private String primaryInsurancePhone;

    @Column(name = "secondary_insurance", length = 100)
    private String secondaryInsurance;

    @Column(name = "secondary_insurance_id", length = 50)
    private String secondaryInsuranceId;

    @Column(name = "secondary_insurance_group", length = 50)
    private String secondaryInsuranceGroup;

    @Column(name = "medicaid_number", length = 50)
    private String medicaidNumber;

    @Column(name = "medicare_number", length = 50)
    private String medicareNumber;

    // Physician Information
    @Column(name = "primary_physician_name", length = 100)
    private String primaryPhysicianName;

    @Column(name = "primary_physician_npi", length = 10)
    private String primaryPhysicianNpi;

    @Column(name = "primary_physician_phone", length = 20)
    private String primaryPhysicianPhone;

    @Column(name = "primary_physician_fax", length = 20)
    private String primaryPhysicianFax;

    @Column(name = "primary_physician_address", length = 255)
    private String primaryPhysicianAddress;

    @Column(name = "primary_physician_city", length = 100)
    private String primaryPhysicianCity;

    @Column(name = "primary_physician_state", length = 2)
    private String primaryPhysicianState;

    @Column(name = "primary_physician_zip", length = 10)
    private String primaryPhysicianZip;

    @Column(name = "referring_physician_name", length = 100)
    private String referringPhysicianName;

    @Column(name = "referring_physician_npi", length = 10)
    private String referringPhysicianNpi;

    @Column(name = "referring_physician_phone", length = 20)
    private String referringPhysicianPhone;

    // Diagnosis Information
    @Column(name = "primary_diagnosis", length = 200)
    private String primaryDiagnosis;

    @Column(name = "primary_diagnosis_icd10", length = 20)
    private String primaryDiagnosisIcd10;

    @Column(name = "primary_diagnosis_description", columnDefinition = "TEXT")
    private String primaryDiagnosisDescription;

    @Column(name = "secondary_diagnosis1", length = 200)
    private String secondaryDiagnosis1;

    @Column(name = "secondary_diagnosis1_icd10", length = 20)
    private String secondaryDiagnosis1Icd10;

    @Column(name = "secondary_diagnosis2", length = 200)
    private String secondaryDiagnosis2;

    @Column(name = "secondary_diagnosis2_icd10", length = 20)
    private String secondaryDiagnosis2Icd10;

    @Column(name = "secondary_diagnosis3", length = 200)
    private String secondaryDiagnosis3;

    @Column(name = "secondary_diagnosis3_icd10", length = 20)
    private String secondaryDiagnosis3Icd10;

    // Service Information
    @Column(name = "service_requested", length = 50)
    private String serviceRequested;

    @Column(name = "service_start_date")
    private LocalDate serviceStartDate;

    @Column(name = "expected_frequency", length = 50)
    private String expectedFrequency;

    @Column(name = "expected_duration", length = 50)
    private String expectedDuration;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // Clinical Information
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "functional_limitations", columnDefinition = "TEXT")
    private String functionalLimitations;

    @Column(name = "equipment_needs", columnDefinition = "TEXT")
    private String equipmentNeeds;

    // Status
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING"; // PENDING, ADMITTED, DECLINED

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Link to patient if admitted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
}

