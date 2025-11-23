package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "physician_orders", indexes = {
    @Index(name = "idx_physorder_poc", columnList = "plan_of_care_id"),
    @Index(name = "idx_physorder_type", columnList = "order_type"),
    @Index(name = "idx_physorder_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhysicianOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_of_care_id", nullable = false)
    private PlanOfCare planOfCare;

    // Order Information
    @Column(name = "order_number", length = 50)
    private String orderNumber; // Unique order tracking number

    @Column(name = "order_type", nullable = false, length = 50)
    private String orderType; 
    // e.g., "SKILLED_NURSING", "PHYSICAL_THERAPY", "OCCUPATIONAL_THERAPY",
    // "SPEECH_THERAPY", "HOME_HEALTH_AIDE", "MEDICAL_SOCIAL_WORKER",
    // "DME" (Durable Medical Equipment), "MEDICATION", "LAB_WORK"

    @Column(name = "order_description", length = 2000, nullable = false)
    private String orderDescription;

    // Discipline for Service Orders
    @Column(name = "discipline", length = 20)
    private String discipline; // RN, PT, OT, ST, HHA, MSW (for service orders)

    // Frequency and Duration
    @Column(name = "frequency", length = 200)
    private String frequency; 
    // e.g., "3x per week", "Daily", "BID", "PRN"

    @Column(name = "duration", length = 100)
    private String duration; 
    // e.g., "60 days", "4 weeks", "Until further notice"

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Physician Information
    @Column(name = "ordering_physician_name", length = 200)
    private String orderingPhysicianName;

    @Column(name = "ordering_physician_npi", length = 20)
    private String orderingPhysicianNpi;

    @Column(name = "ordering_physician_phone", length = 20)
    private String orderingPhysicianPhone;

    @Column(name = "order_date")
    private LocalDate orderDate;

    // Signature Status
    @Column(name = "signature_required")
    private Boolean signatureRequired;

    @Column(name = "signature_obtained")
    private Boolean signatureObtained;

    @Column(name = "signature_date")
    private LocalDate signatureDate;

    @Column(name = "verbal_order")
    private Boolean verbalOrder; // If order was received verbally

    @Column(name = "verbal_order_received_by", length = 200)
    private String verbalOrderReceivedBy; // RN who took verbal order

    @Column(name = "verbal_order_date")
    private LocalDate verbalOrderDate;

    // Status
    @Column(name = "status", nullable = false, length = 30)
    private String status; 
    // PENDING, ACTIVE, COMPLETED, EXPIRED, CANCELLED, RENEWED, DISCONTINUED

    @Column(name = "status_reason", length = 500)
    private String statusReason;

    @Column(name = "is_active")
    private Boolean isActive;

    // Clinical Information
    @Column(name = "diagnosis_codes", length = 500)
    private String diagnosisCodes; // Related ICD-10 codes

    @Column(name = "clinical_justification", length = 1000)
    private String clinicalJustification; // Medical necessity justification

    @Column(name = "precautions", length = 500)
    private String precautions;

    @Column(name = "contraindications", length = 500)
    private String contraindications;

    // Renewal Information
    @Column(name = "renewable")
    private Boolean renewable;

    @Column(name = "renewal_date")
    private LocalDate renewalDate; // Date when renewal is needed

    @Column(name = "renewed_by_order_id")
    private Long renewedByOrderId; // Links to new order if renewed

    // Additional Information
    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "notes", length = 1000)
    private String notes;

    // Helper Methods
    public Boolean isExpired() {
        if (endDate != null) {
            return LocalDate.now().isAfter(endDate);
        }
        return false;
    }

    public Boolean isExpiringSoon() {
        if (endDate != null) {
            long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS
                .between(LocalDate.now(), endDate);
            return daysUntilExpiry <= 7 && daysUntilExpiry >= 0;
        }
        return false;
    }

    public Boolean needsRenewal() {
        if (renewalDate != null && renewable != null && renewable) {
            return LocalDate.now().isAfter(renewalDate) || LocalDate.now().isEqual(renewalDate);
        }
        return false;
    }

    public Boolean needsSignature() {
        return signatureRequired != null && signatureRequired && 
               (signatureObtained == null || !signatureObtained);
    }
}

