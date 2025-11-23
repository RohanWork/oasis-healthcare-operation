package com.oasis.homehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {

    private Long id;
    private String medicalRecordNumber;
    
    // Personal Information
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Integer age;
    private String gender;
    private String ssn;
    private String maritalStatus;
    private String race;
    private String ethnicity;
    private String language;
    
    // Contact Information
    private String phoneNumber;
    private String mobileNumber;
    private String email;
    
    // Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String county;
    private String fullAddress;
    
    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;
    
    // Physician Information
    private String primaryPhysicianName;
    private String primaryPhysicianPhone;
    private String primaryPhysicianNpi;
    
    // Referral Information
    private String referralSource;
    private LocalDate referralDate;
    private String referralDiagnosis;
    
    // Admission Information
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private Long admittingClinicianId;
    private String admittingClinicianName;
    
    // Status
    private String status;
    private String statusReason;
    
    // Clinical Information
    private String allergies;
    private String medications;
    private String medicalHistory;
    private String specialInstructions;
    
    // Organization
    private Long organizationId;
    private String organizationName;
    
    // Insurance
    private List<InsuranceDTO> insurances;
    
    // Episodes
    private List<EpisodeDTO> episodes;
    private EpisodeDTO activeEpisode;
    
    // Metadata
    private LocalDate createdAt;
    private String createdBy;
    private LocalDate updatedAt;
    private String updatedBy;
}

