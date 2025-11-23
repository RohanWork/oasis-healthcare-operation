package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_claims", indexes = {
    @Index(name = "idx_billing_claim_patient", columnList = "patient_id"),
    @Index(name = "idx_billing_claim_episode", columnList = "episode_id"),
    @Index(name = "idx_billing_claim_org", columnList = "organization_id"),
    @Index(name = "idx_billing_claim_status", columnList = "status"),
    @Index(name = "idx_billing_claim_date", columnList = "billing_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingClaim extends BaseEntity {

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_note_id")
    private VisitNote visitNote; // Optional: link to specific visit note

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task; // Optional: link to specific task

    // Claim Information
    @Column(name = "claim_number", unique = true, nullable = false, length = 50)
    private String claimNumber;

    @Column(name = "claim_type", nullable = false, length = 50)
    private String claimType; // RAP (Request for Anticipated Payment), FINAL, ADJUSTMENT

    @Column(name = "billing_date", nullable = false)
    private LocalDate billingDate;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "service_end_date")
    private LocalDate serviceEndDate; // For date ranges

    // Service Information
    @Column(name = "service_code", nullable = false, length = 20)
    private String serviceCode; // CPT/HCPCS code

    @Column(name = "service_description", length = 500)
    private String serviceDescription;

    @Column(name = "units", nullable = false)
    private Integer units; // 15-minute units

    @Column(name = "unit_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitRate;

    @Column(name = "total_charge", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCharge;

    // Insurance Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id")
    private Insurance insurance;

    @Column(name = "insurance_claim_number", length = 50)
    private String insuranceClaimNumber;

    // Status and Workflow
    @Column(name = "status", nullable = false, length = 30)
    private String status; // DRAFT, SUBMITTED, PAID, DENIED, ADJUSTED, VOIDED

    @Column(name = "submitted_date")
    private LocalDate submittedDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "denied_date")
    private LocalDate deniedDate;

    @Column(name = "denial_reason", length = 1000)
    private String denialReason;

    // Financial Information
    @Column(name = "patient_responsibility", precision = 10, scale = 2)
    private BigDecimal patientResponsibility;

    @Column(name = "insurance_payment", precision = 10, scale = 2)
    private BigDecimal insurancePayment;

    @Column(name = "adjustment_amount", precision = 10, scale = 2)
    private BigDecimal adjustmentAmount;

    @Column(name = "adjustment_reason", length = 500)
    private String adjustmentReason;

    // Additional Information
    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "billed_by_user_id")
    private Long billedByUserId;

    @Column(name = "billed_at")
    private LocalDateTime billedAt;

    // Helper Methods
    public Boolean isPaid() {
        return "PAID".equals(status);
    }

    public Boolean isDenied() {
        return "DENIED".equals(status);
    }

    public Boolean canBeEdited() {
        return "DRAFT".equals(status) || "DENIED".equals(status);
    }

    public BigDecimal getOutstandingBalance() {
        if (totalCharge == null) return BigDecimal.ZERO;
        BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        BigDecimal adjusted = adjustmentAmount != null ? adjustmentAmount : BigDecimal.ZERO;
        return totalCharge.subtract(paid).subtract(adjusted);
    }
}

