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
public class InsuranceDTO {

    private Long id;
    private Long patientId;
    
    // Insurance Type
    private String insuranceType;
    private Integer priorityOrder;
    
    // Insurance Company
    private String insuranceCompany;
    private String payerId;
    
    // Policy Information
    private String policyNumber;
    private String groupNumber;
    private String planName;
    private String planType;
    
    // Coverage Dates
    private LocalDate effectiveDate;
    private LocalDate terminationDate;
    
    // Subscriber Information
    private String subscriberName;
    private String subscriberRelationship;
    private LocalDate subscriberDateOfBirth;
    private String subscriberSsn;
    private String subscriberGender;
    
    // Contact Information
    private String insurancePhone;
    private String insuranceFax;
    private String insuranceAddress;
    
    // Authorization
    private String authorizationNumber;
    private LocalDate authorizationStartDate;
    private LocalDate authorizationEndDate;
    private Integer authorizedVisits;
    
    // Verification
    private String verificationStatus;
    private LocalDate verificationDate;
    private String verifiedBy;
    private String verificationNotes;
    
    // Copay and Deductible
    private Double copayAmount;
    private Double deductibleAmount;
    private Double deductibleMet;
    
    // Status
    private Boolean isActive;
    private Boolean isCurrentlyActive;
    private Boolean needsVerification;
    private String notes;
}

