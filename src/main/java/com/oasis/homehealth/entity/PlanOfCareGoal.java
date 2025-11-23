package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "plan_of_care_goals", indexes = {
    @Index(name = "idx_pocgoal_poc", columnList = "plan_of_care_id"),
    @Index(name = "idx_pocgoal_category", columnList = "goal_category"),
    @Index(name = "idx_pocgoal_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanOfCareGoal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_of_care_id", nullable = false)
    private PlanOfCare planOfCare;

    // Goal Information
    @Column(name = "goal_category", length = 100)
    private String goalCategory; 
    // e.g., "ADL (Activities of Daily Living)", "IADL (Instrumental ADL)", 
    // "Mobility", "Safety", "Pain Management", "Medication Compliance",
    // "Wound Healing", "Knowledge Deficit"

    @Column(name = "goal_type", length = 50)
    private String goalType; // SHORT_TERM, LONG_TERM, DISCHARGE

    @Column(name = "goal_description", length = 1000, nullable = false)
    private String goalDescription;

    // Measurable Outcome
    @Column(name = "measurable_outcome", length = 500)
    private String measurableOutcome; 
    // e.g., "Patient will ambulate 50 feet with walker independently"

    @Column(name = "baseline_measure", length = 200)
    private String baselineMeasure; 
    // e.g., "Currently ambulates 20 feet with mod assist"

    @Column(name = "target_measure", length = 200)
    private String targetMeasure; 
    // e.g., "50 feet independently"

    // Timeline
    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "review_date")
    private LocalDate reviewDate; // Date to review progress

    // Status and Progress
    @Column(name = "status", length = 30)
    private String status; // NOT_STARTED, IN_PROGRESS, ACHIEVED, PARTIALLY_ACHIEVED, NOT_ACHIEVED, DISCONTINUED

    @Column(name = "progress_percentage")
    private Integer progressPercentage; // 0-100

    @Column(name = "progress_notes", length = 1000)
    private String progressNotes;

    @Column(name = "achieved_date")
    private LocalDate achievedDate;

    // Responsible Discipline
    @Column(name = "responsible_discipline", length = 20)
    private String responsibleDiscipline; // RN, PT, OT, ST, HHA, MSW

    // Priority
    @Column(name = "priority", length = 20)
    private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "is_active")
    private Boolean isActive;

    // Additional Information
    @Column(name = "barriers", length = 500)
    private String barriers; // Barriers to achieving goal

    @Column(name = "interventions_related", length = 500)
    private String interventionsRelated; // Related interventions

    @Column(name = "notes", length = 500)
    private String notes;

    // Helper Methods
    public Boolean isOverdue() {
        if (targetDate != null && !status.equals("ACHIEVED") && !status.equals("DISCONTINUED")) {
            return LocalDate.now().isAfter(targetDate);
        }
        return false;
    }

    public Integer getDaysUntilTarget() {
        if (targetDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        }
        return null;
    }
}

