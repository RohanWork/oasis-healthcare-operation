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
public class PatientRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "SSN must be in format XXX-XX-XXXX")
    private String ssn;

    private String maritalStatus;
    private String race;
    private String ethnicity;
    private String language;

    // Contact Information
    @Pattern(regexp = "^$|^\\d{3}-\\d{3}-\\d{4}$", message = "Phone number must be in format XXX-XXX-XXXX or empty")
    private String phoneNumber;

    @Pattern(regexp = "^$|^\\d{3}-\\d{3}-\\d{4}$", message = "Mobile number must be in format XXX-XXX-XXXX or empty")
    private String mobileNumber;

    @Email(message = "Email must be valid")
    private String email;

    // Address
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must be 2 characters")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Zip code must be in format XXXXX or XXXXX-XXXX")
    private String zipCode;

    private String county;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactRelationship;
    
    @Pattern(regexp = "^$|^\\d{3}-\\d{3}-\\d{4}$", message = "Emergency contact phone must be in format XXX-XXX-XXXX or empty")
    private String emergencyContactPhone;

    // Physician Information
    private String primaryPhysicianName;
    
    @Pattern(regexp = "^$|^\\d{3}-\\d{3}-\\d{4}$", message = "Primary physician phone must be in format XXX-XXX-XXXX or empty")
    private String primaryPhysicianPhone;
    
    @Pattern(regexp = "^$|^\\d{10}$", message = "NPI Number must be exactly 10 digits or empty")
    @Size(max = 10, message = "NPI Number must not exceed 10 characters")
    private String primaryPhysicianNpi;

    // Referral Information
    private String referralSource;
    private LocalDate referralDate;
    private String referralDiagnosis;

    // Admission Information
    private LocalDate admissionDate;
    private Long admittingClinicianId;

    // Status
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PENDING|ACTIVE|DISCHARGED|DECEASED|TRANSFERRED", 
             message = "Status must be PENDING, ACTIVE, DISCHARGED, DECEASED, or TRANSFERRED")
    private String status;

    private String statusReason;

    // Clinical Information
    private String allergies;
    private String medications;
    private String medicalHistory;
    private String specialInstructions;
}

