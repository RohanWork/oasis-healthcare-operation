package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plan_of_care", indexes = {
    @Index(name = "idx_poc_patient", columnList = "patient_id"),
    @Index(name = "idx_poc_episode", columnList = "episode_id"),
    @Index(name = "idx_poc_oasis", columnList = "oasis_assessment_id"),
    @Index(name = "idx_poc_status", columnList = "status"),
    @Index(name = "idx_poc_org", columnList = "organization_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanOfCare extends BaseEntity {

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oasis_assessment_id")
    private OasisAssessmentComplete oasisAssessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // POC Metadata
    @Column(name = "poc_number", unique = true, nullable = false, length = 30)
    private String pocNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "certification_period_days")
    private Integer certificationPeriodDays; // Usually 60 days

    // Diagnosis Information
    @Column(name = "primary_diagnosis_code", length = 20)
    private String primaryDiagnosisCode;

    @Column(name = "primary_diagnosis_description", length = 500)
    private String primaryDiagnosisDescription;

    @Column(name = "secondary_diagnosis_code", length = 20)
    private String secondaryDiagnosisCode;

    @Column(name = "secondary_diagnosis_description", length = 500)
    private String secondaryDiagnosisDescription;

    @Column(name = "other_diagnoses", length = 2000)
    private String otherDiagnoses; // JSON or comma-separated

    // Functional Status Summary
    @Column(name = "functional_limitations", length = 1000)
    private String functionalLimitations;

    @Column(name = "safety_measures", length = 1000)
    private String safetyMeasures;

    @Column(name = "nutritional_requirements", length = 500)
    private String nutritionalRequirements;

    // Medications
    @Column(name = "medication_list", length = 2000)
    private String medicationList;

    @Column(name = "medication_management_needed")
    private Boolean medicationManagementNeeded;

    // Status and Workflow
    @Column(name = "status", nullable = false, length = 20)
    private String status; // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, ACTIVE, COMPLETED

    @Column(name = "status_reason", length = 500)
    private String statusReason;

    // Physician Information
    @Column(name = "physician_name", length = 200)
    private String physicianName;

    @Column(name = "physician_phone", length = 20)
    private String physicianPhone;

    @Column(name = "physician_signature_required")
    private Boolean physicianSignatureRequired;

    @Column(name = "physician_signed_date")
    private LocalDate physicianSignedDate;

    @Column(name = "physician_signature_obtained")
    private Boolean physicianSignatureObtained;

    // Approval Workflow
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_clinician_id")
    private User createdByClinician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejected_by_user_id")
    private User rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    // Additional Information
    @Column(name = "special_instructions", length = 2000)
    private String specialInstructions;

    @Column(name = "dme_equipment", length = 1000)
    private String dmeEquipment; // Durable Medical Equipment

    @Column(name = "notes", length = 2000)
    private String notes;

    // Child Relationships
    @OneToMany(mappedBy = "planOfCare", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlanOfCareFrequency> frequencies = new HashSet<>();

    @OneToMany(mappedBy = "planOfCare", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlanOfCareIntervention> interventions = new HashSet<>();

    @OneToMany(mappedBy = "planOfCare", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlanOfCareGoal> goals = new HashSet<>();

    @OneToMany(mappedBy = "planOfCare", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PhysicianOrder> physicianOrders = new HashSet<>();

    // Helper Methods
    public void addFrequency(PlanOfCareFrequency frequency) {
        frequencies.add(frequency);
        frequency.setPlanOfCare(this);
    }

    public void removeFrequency(PlanOfCareFrequency frequency) {
        frequencies.remove(frequency);
        frequency.setPlanOfCare(null);
    }

    public void addIntervention(PlanOfCareIntervention intervention) {
        interventions.add(intervention);
        intervention.setPlanOfCare(this);
    }

    public void removeIntervention(PlanOfCareIntervention intervention) {
        interventions.remove(intervention);
        intervention.setPlanOfCare(null);
    }

    public void addGoal(PlanOfCareGoal goal) {
        goals.add(goal);
        goal.setPlanOfCare(this);
    }

    public void removeGoal(PlanOfCareGoal goal) {
        goals.remove(goal);
        goal.setPlanOfCare(null);
    }

    public void addPhysicianOrder(PhysicianOrder order) {
        physicianOrders.add(order);
        order.setPlanOfCare(this);
    }

    public void removePhysicianOrder(PhysicianOrder order) {
        physicianOrders.remove(order);
        order.setPlanOfCare(null);
    }

    public Integer getRemainingDays() {
        if (endDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        }
        return null;
    }

    public Boolean isExpiringSoon() {
        Integer remaining = getRemainingDays();
        return remaining != null && remaining <= 7 && remaining >= 0;
    }

    public Boolean isExpired() {
        if (endDate != null) {
            return LocalDate.now().isAfter(endDate);
        }
        return false;
    }
}

