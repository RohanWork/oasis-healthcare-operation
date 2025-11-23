package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "plan_of_care_frequencies", indexes = {
    @Index(name = "idx_pocfreq_poc", columnList = "plan_of_care_id"),
    @Index(name = "idx_pocfreq_discipline", columnList = "discipline_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanOfCareFrequency extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_of_care_id", nullable = false)
    private PlanOfCare planOfCare;

    // Discipline Information
    @Column(name = "discipline_type", nullable = false, length = 20)
    private String disciplineType; // RN, PT, OT, ST, HHA, MSW

    @Column(name = "discipline_description", length = 100)
    private String disciplineDescription; // "Registered Nurse", "Physical Therapist", etc.

    // Frequency Information
    @Column(name = "frequency_code", length = 20)
    private String frequencyCode; // e.g., "3W3" = 3 visits per week for 3 weeks

    @Column(name = "visits_per_week")
    private Integer visitsPerWeek;

    @Column(name = "number_of_weeks")
    private Integer numberOfWeeks;

    @Column(name = "total_visits")
    private Integer totalVisits; // Calculated: visitsPerWeek * numberOfWeeks

    @Column(name = "completed_visits")
    private Integer completedVisits; // Tracking

    // Visit Duration
    @Column(name = "estimated_minutes_per_visit")
    private Integer estimatedMinutesPerVisit;

    // Time Period
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Status
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "status", length = 20)
    private String status; // PLANNED, ACTIVE, COMPLETED, CANCELLED

    // Additional Information
    @Column(name = "notes", length = 500)
    private String notes;

    // Helper Methods
    public void calculateTotalVisits() {
        if (visitsPerWeek != null && numberOfWeeks != null) {
            this.totalVisits = visitsPerWeek * numberOfWeeks;
        }
    }

    public Integer getRemainingVisits() {
        if (totalVisits != null && completedVisits != null) {
            return totalVisits - completedVisits;
        }
        return totalVisits;
    }

    public Double getCompletionPercentage() {
        if (totalVisits != null && totalVisits > 0 && completedVisits != null) {
            return (completedVisits.doubleValue() / totalVisits.doubleValue()) * 100;
        }
        return 0.0;
    }
}

