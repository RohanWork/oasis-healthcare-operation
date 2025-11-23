package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "visit_notes", indexes = {
    @Index(name = "idx_visitnote_task", columnList = "task_id"),
    @Index(name = "idx_visitnote_patient", columnList = "patient_id"),
    @Index(name = "idx_visitnote_clinician", columnList = "clinician_id"),
    @Index(name = "idx_visitnote_status", columnList = "status"),
    @Index(name = "idx_visitnote_date", columnList = "visit_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitNote extends BaseEntity {

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, unique = true)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinician_id", nullable = false)
    private User clinician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // Visit Information
    @Column(name = "visit_type", nullable = false, length = 50)
    private String visitType; // RN_VISIT, PT_VISIT, OT_VISIT, ST_VISIT, HHA_VISIT, etc.

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "visit_start_time")
    private LocalTime visitStartTime;

    @Column(name = "visit_end_time")
    private LocalTime visitEndTime;

    @Column(name = "visit_duration_minutes")
    private Integer visitDurationMinutes;

    // Clinical Documentation (Basic - will be expanded in Phase 5)
    @Column(name = "chief_complaint", length = 500)
    private String chiefComplaint;

    @Column(name = "vital_signs", length = 1000)
    private String vitalSigns; // JSON: {BP, HR, Temp, RR, O2Sat, Pain, Weight}

    @Column(name = "assessment_findings", length = 2000)
    private String assessmentFindings;

    @Column(name = "interventions_provided", length = 2000)
    private String interventionsProvided;

    @Column(name = "patient_response", length = 1000)
    private String patientResponse;

    @Column(name = "teaching_provided", length = 1000)
    private String teachingProvided;

    @Column(name = "follow_up_plan", length = 1000)
    private String followUpPlan;

    @Column(name = "next_visit_date")
    private LocalDate nextVisitDate;

    // Travel Information
    @Column(name = "mileage")
    private Double mileage;

    @Column(name = "travel_time_minutes")
    private Integer travelTimeMinutes;

    // Status and Workflow
    @Column(name = "status", nullable = false, length = 20)
    private String status; // DRAFT, SUBMITTED, APPROVED, REJECTED, RETURNED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id")
    private User submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "qa_comments", length = 1000)
    private String qaComments;

    @Column(name = "returned_for_correction_at")
    private LocalDateTime returnedForCorrectionAt;

    @Column(name = "correction_comments", length = 1000)
    private String correctionComments;

    @Column(name = "revision_number")
    private Integer revisionNumber; // Track how many times edited

    // Additional Information
    @Column(name = "special_notes", length = 1000)
    private String specialNotes;

    @Column(name = "physician_contacted")
    private Boolean physicianContacted;

    @Column(name = "physician_contact_reason", length = 500)
    private String physicianContactReason;

    // Helper Methods
    public Boolean isLocked() {
        return status != null && (status.equals("APPROVED") || status.equals("QA_APPROVED"));
    }

    public Boolean canBeEdited() {
        return status != null && (status.equals("DRAFT") || status.equals("RETURNED"));
    }

    public Boolean needsQAReview() {
        return status != null && status.equals("SUBMITTED");
    }

    public void calculateVisitDuration() {
        if (visitStartTime != null && visitEndTime != null) {
            this.visitDurationMinutes = (int) java.time.Duration.between(visitStartTime, visitEndTime).toMinutes();
        }
    }
}

