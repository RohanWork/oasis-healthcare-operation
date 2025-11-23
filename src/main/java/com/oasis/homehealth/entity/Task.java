package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_patient", columnList = "patient_id"),
    @Index(name = "idx_task_episode", columnList = "episode_id"),
    @Index(name = "idx_task_assigned", columnList = "assigned_to_user_id"),
    @Index(name = "idx_task_scheduled_date", columnList = "scheduled_date"),
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_type", columnList = "task_type"),
    @Index(name = "idx_task_org", columnList = "organization_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity {

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_of_care_id")
    private PlanOfCare planOfCare; // Source POC that generated this task

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo; // Clinician assigned to this task

    // Task Metadata
    @Column(name = "task_number", unique = true, nullable = false, length = 30)
    private String taskNumber;

    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType; 
    // RN_VISIT, PT_VISIT, OT_VISIT, ST_VISIT, HHA_VISIT, MSW_VISIT,
    // SUPERVISORY_VISIT, OASIS_RECERT, OASIS_DISCHARGE, PHYSICIAN_ORDER_RENEWAL

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    // Scheduling Information
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_start_time")
    private LocalTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalTime scheduledEndTime;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes; // e.g., 60 minutes

    // Actual Time Tracking
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    // Status
    @Column(name = "status", nullable = false, length = 30)
    private String status; 
    // SCHEDULED, IN_PROGRESS, COMPLETED_PENDING_QA, QA_APPROVED, 
    // CANCELLED, RESCHEDULED, MISSED, NO_SHOW

    @Column(name = "status_reason", length = 500)
    private String statusReason;

    // Priority
    @Column(name = "priority", length = 20)
    private String priority; // LOW, NORMAL, HIGH, URGENT

    @Column(name = "is_urgent")
    private Boolean isUrgent;

    // Completion Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_user_id")
    private User completedBy;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completion_notes", length = 1000)
    private String completionNotes;

    // QA Review
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_reviewed_by_user_id")
    private User qaReviewedBy;

    @Column(name = "qa_reviewed_at")
    private LocalDateTime qaReviewedAt;

    @Column(name = "qa_comments", length = 1000)
    private String qaComments;

    @Column(name = "qa_status", length = 20)
    private String qaStatus; // PENDING, APPROVED, RETURNED, REJECTED

    // Cancellation/Rescheduling
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by_user_id")
    private User cancelledBy;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "original_scheduled_date")
    private LocalDate originalScheduledDate; // For tracking reschedules

    @Column(name = "reschedule_count")
    private Integer rescheduleCount; // How many times rescheduled

    // Visit Details
    @Column(name = "visit_location", length = 200)
    private String visitLocation; // Usually patient's home

    @Column(name = "travel_time_minutes")
    private Integer travelTimeMinutes;

    @Column(name = "mileage")
    private Double mileage;

    // Recurrence (for recurring visits)
    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "recurrence_pattern", length = 100)
    private String recurrencePattern; // e.g., "WEEKLY", "BI_WEEKLY", "DAILY"

    @Column(name = "parent_task_id")
    private Long parentTaskId; // If generated from recurring pattern

    // Reminders & Notifications
    @Column(name = "reminder_sent")
    private Boolean reminderSent;

    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;

    @Column(name = "confirmation_required")
    private Boolean confirmationRequired;

    @Column(name = "confirmation_received")
    private Boolean confirmationReceived;

    // Additional Information
    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "patient_availability", length = 500)
    private String patientAvailability; // e.g., "Not available before 10 AM"

    @Column(name = "notes", length = 1000)
    private String notes;

    // Billable Information (for future billing module)
    @Column(name = "is_billable")
    private Boolean isBillable;

    @Column(name = "billing_code", length = 20)
    private String billingCode; // CPT/HCPCS code

    @Column(name = "billing_units")
    private Integer billingUnits; // 15-minute units

    @Column(name = "billed")
    private Boolean billed;

    // Helper Methods
    public Boolean isOverdue() {
        if (scheduledDate != null && !status.equals("COMPLETED_PENDING_QA") && 
            !status.equals("QA_APPROVED") && !status.equals("CANCELLED")) {
            return LocalDate.now().isAfter(scheduledDate);
        }
        return false;
    }

    public Boolean isDueToday() {
        return scheduledDate != null && scheduledDate.equals(LocalDate.now());
    }

    public Boolean isDueTomorrow() {
        return scheduledDate != null && scheduledDate.equals(LocalDate.now().plusDays(1));
    }

    public Integer getDaysUntilDue() {
        if (scheduledDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), scheduledDate);
        }
        return null;
    }

    public String getDisciplineCode() {
        if (taskType != null) {
            if (taskType.startsWith("RN_")) return "RN";
            if (taskType.startsWith("PT_")) return "PT";
            if (taskType.startsWith("OT_")) return "OT";
            if (taskType.startsWith("ST_")) return "ST";
            if (taskType.startsWith("HHA_")) return "HHA";
            if (taskType.startsWith("MSW_")) return "MSW";
        }
        return "OTHER";
    }

    public Boolean canBeEdited() {
        return status != null && (status.equals("SCHEDULED") || status.equals("RESCHEDULED"));
    }

    public Boolean canBeCancelled() {
        return status != null && !status.equals("COMPLETED_PENDING_QA") && 
               !status.equals("QA_APPROVED") && !status.equals("CANCELLED");
    }

    public Boolean needsQAReview() {
        return status != null && status.equals("COMPLETED_PENDING_QA");
    }

    public void calculateActualDuration() {
        if (actualStartTime != null && actualEndTime != null) {
            this.actualDurationMinutes = (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
    }

    public void calculateBillingUnits() {
        if (actualDurationMinutes != null) {
            // Medicare billing: 15-minute units
            this.billingUnits = (int) Math.ceil(actualDurationMinutes / 15.0);
        }
    }
}
