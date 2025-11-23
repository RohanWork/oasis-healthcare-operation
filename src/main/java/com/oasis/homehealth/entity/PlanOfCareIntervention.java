package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plan_of_care_interventions", indexes = {
    @Index(name = "idx_pocint_poc", columnList = "plan_of_care_id"),
    @Index(name = "idx_pocint_category", columnList = "intervention_category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanOfCareIntervention extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_of_care_id", nullable = false)
    private PlanOfCare planOfCare;

    // Intervention Information
    @Column(name = "intervention_category", length = 100)
    private String interventionCategory; 
    // e.g., "Medication Management", "Wound Care", "Patient Education", 
    // "Safety Assessment", "Pain Management", "Fall Prevention"

    @Column(name = "intervention_code", length = 50)
    private String interventionCode; // Optional code for standardization

    @Column(name = "intervention_description", length = 1000, nullable = false)
    private String interventionDescription;

    // Frequency and Duration
    @Column(name = "frequency", length = 100)
    private String frequency; // e.g., "Daily", "3x per week", "PRN", "Each visit"

    @Column(name = "duration", length = 100)
    private String duration; // e.g., "60 days", "Until discharge", "As needed"

    // Responsible Discipline
    @Column(name = "responsible_discipline", length = 20)
    private String responsibleDiscipline; // RN, PT, OT, ST, HHA, MSW

    @Column(name = "collaborative_disciplines", length = 200)
    private String collaborativeDisciplines; // e.g., "RN,PT" if multiple disciplines involved

    // Priority and Status
    @Column(name = "priority", length = 20)
    private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "status", length = 20)
    private String status; // ACTIVE, COMPLETED, DISCONTINUED, ON_HOLD

    @Column(name = "is_active")
    private Boolean isActive;

    // Expected Outcome
    @Column(name = "expected_outcome", length = 500)
    private String expectedOutcome;

    // Additional Information
    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    @Column(name = "contraindications", length = 500)
    private String contraindications;

    @Column(name = "notes", length = 500)
    private String notes;
}

