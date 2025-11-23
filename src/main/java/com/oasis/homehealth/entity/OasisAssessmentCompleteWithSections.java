package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import com.oasis.homehealth.entity.embedded.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * COMPLETE OASIS-E1 Assessment Entity with ALL Sections
 * Based on official CMS OASIS-E1 document
 * Uses embedded objects for better organization
 */
@Entity
@Table(name = "oasis_assessments_e1_complete", indexes = {
    @Index(name = "idx_oasis_e1_patient", columnList = "patient_id"),
    @Index(name = "idx_oasis_e1_episode", columnList = "episode_id"),
    @Index(name = "idx_oasis_e1_status", columnList = "status"),
    @Index(name = "idx_oasis_e1_type", columnList = "assessment_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OasisAssessmentCompleteWithSections extends BaseEntity {
    
    /**
     * Helper method to check if assessment is locked
     */
    public boolean isLocked() {
        return this.lockedAt != null;
    }
    
    /**
     * Helper method to check if assessment can be edited
     */
    public boolean canEdit() {
        return "DRAFT".equals(this.status) || "REJECTED".equals(this.status);
    }
    
    /**
     * Helper method to check if deleted (delegates to BaseEntity)
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(super.getIsDeleted());
    }

    // ============================================
    // RELATIONSHIPS
    // ============================================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinician_id")
    private User clinician;

    // ============================================
    // ASSESSMENT METADATA
    // ============================================
    
    @Column(name = "assessment_type", nullable = false, length = 50)
    private String assessmentType; // SOC, ROC, RECERT, TRANSFER, DISCHARGE

    @Column(name = "assessment_reason", length = 100)
    private String assessmentReason;

    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // DRAFT, SUBMITTED, QA_REVIEW, APPROVED, REJECTED, LOCKED

    @Column(name = "completion_percentage")
    private Integer completionPercentage;

    @Column(name = "last_auto_saved")
    private LocalDateTime lastAutoSaved;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "clinician_name", length = 200)
    private String clinicianName;

    // QA Workflow
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

    @Column(name = "qa_comments", length = 2000)
    private String qaComments;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    // Section Completion Tracking
    @Column(name = "section_completion", columnDefinition = "TEXT")
    private String sectionCompletion; // JSON

    // Skip Logic Tracking
    @Column(name = "skipped_fields", columnDefinition = "TEXT")
    private String skippedFields; // JSON

    // ============================================
    // ALL SECTIONS AS EMBEDDED OBJECTS
    // ============================================

    @Embedded
    private SectionA sectionA;

    @Embedded
    private SectionB sectionB;

    @Embedded
    private SectionC sectionC;

    @Embedded
    private SectionD sectionD;

    @Embedded
    private SectionE sectionE;

    @Embedded
    private SectionF sectionF;

    @Embedded
    private SectionG sectionG;

    @Embedded
    private SectionGG sectionGG;

    @Embedded
    private SectionH sectionH;

    @Embedded
    private SectionI sectionI;

    @Embedded
    private SectionJ sectionJ;

    @Embedded
    private SectionK sectionK;

    @Embedded
    private SectionM sectionM;

    @Embedded
    private SectionN sectionN;

    @Embedded
    private SectionO sectionO;

    @Embedded
    private SectionQ sectionQ;

    // ============================================
    // HELPER METHODS
    // ============================================
    
    public boolean needsQAReview() {
        return "SUBMITTED".equals(status);
    }
}

