package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "insurances", indexes = {
    @Index(name = "idx_insurance_patient", columnList = "patient_id"),
    @Index(name = "idx_insurance_policy", columnList = "policy_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Insurance Type
    @Column(name = "insurance_type", nullable = false, length = 20)
    private String insuranceType; // PRIMARY, SECONDARY, TERTIARY

    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder; // 1 = Primary, 2 = Secondary, etc.

    // Insurance Company
    @Column(name = "insurance_company", nullable = false, length = 200)
    private String insuranceCompany;

    @Column(name = "payer_id", length = 50)
    private String payerId;

    // Policy Information
    @Column(name = "policy_number", nullable = false, length = 50)
    private String policyNumber;

    @Column(name = "group_number", length = 50)
    private String groupNumber;

    @Column(name = "plan_name", length = 200)
    private String planName;

    @Column(name = "plan_type", length = 50)
    private String planType; // MEDICARE, MEDICAID, COMMERCIAL, HMO, PPO

    // Coverage Dates
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    // Subscriber Information
    @Column(name = "subscriber_name", length = 100)
    private String subscriberName;

    @Column(name = "subscriber_relationship", length = 50)
    private String subscriberRelationship; // SELF, SPOUSE, CHILD, OTHER

    @Column(name = "subscriber_dob")
    private LocalDate subscriberDateOfBirth;

    @Column(name = "subscriber_ssn", length = 11)
    private String subscriberSsn;

    @Column(name = "subscriber_gender", length = 10)
    private String subscriberGender;

    // Contact Information
    @Column(name = "insurance_phone", length = 20)
    private String insurancePhone;

    @Column(name = "insurance_fax", length = 20)
    private String insuranceFax;

    @Column(name = "insurance_address", length = 500)
    private String insuranceAddress;

    // Authorization
    @Column(name = "authorization_number", length = 50)
    private String authorizationNumber;

    @Column(name = "authorization_start_date")
    private LocalDate authorizationStartDate;

    @Column(name = "authorization_end_date")
    private LocalDate authorizationEndDate;

    @Column(name = "authorized_visits")
    private Integer authorizedVisits;

    // Verification
    @Column(name = "verification_status", length = 20)
    private String verificationStatus; // PENDING, VERIFIED, FAILED

    @Column(name = "verification_date")
    private LocalDate verificationDate;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verification_notes", length = 1000)
    private String verificationNotes;

    // Copay and Deductible
    @Column(name = "copay_amount")
    private Double copayAmount;

    @Column(name = "deductible_amount")
    private Double deductibleAmount;

    @Column(name = "deductible_met")
    private Double deductibleMet;

    // Status
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "notes", length = 1000)
    private String notes;

    // Helper methods
    public boolean isCurrentlyActive() {
        LocalDate now = LocalDate.now();
        if (effectiveDate != null && effectiveDate.isAfter(now)) {
            return false;
        }
        if (terminationDate != null && terminationDate.isBefore(now)) {
            return false;
        }
        return isActive != null && isActive;
    }

    public boolean needsVerification() {
        return verificationStatus == null || 
               "PENDING".equals(verificationStatus) || 
               "FAILED".equals(verificationStatus);
    }
}

