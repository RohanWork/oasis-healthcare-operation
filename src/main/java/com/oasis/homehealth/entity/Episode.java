package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "episodes", indexes = {
    @Index(name = "idx_episode_patient", columnList = "patient_id"),
    @Index(name = "idx_episode_org", columnList = "organization_id"),
    @Index(name = "idx_episode_status", columnList = "status"),
    @Index(name = "idx_episode_dates", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode extends BaseEntity {

    // Patient Reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Organization (Multi-tenant)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // Episode Number
    @Column(name = "episode_number", unique = true, nullable = false, length = 20)
    private String episodeNumber;

    // Episode Type
    @Column(name = "episode_type", nullable = false, length = 50)
    private String episodeType; // ADMISSION, RECERTIFICATION, RESUMPTION_OF_CARE

    // Dates
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "certification_start_date", nullable = false)
    private LocalDate certificationStartDate;

    @Column(name = "certification_end_date", nullable = false)
    private LocalDate certificationEndDate;

    @Column(name = "certification_period", nullable = false)
    private Integer certificationPeriod; // Days (usually 60)

    // Diagnosis
    @Column(name = "primary_diagnosis_code", length = 20)
    private String primaryDiagnosisCode; // Can be null initially, set when OASIS is completed

    @Column(name = "primary_diagnosis_description", length = 500)
    private String primaryDiagnosisDescription; // Can be null initially, set when OASIS is completed

    @Column(name = "secondary_diagnosis_codes", length = 500)
    private String secondaryDiagnosisCodes; // Comma-separated

    @Column(name = "secondary_diagnosis_descriptions", length = 2000)
    private String secondaryDiagnosisDescriptions;

    // Clinical Information
    @Column(name = "admitting_diagnosis", length = 500)
    private String admittingDiagnosis;

    @Column(name = "surgical_procedures", length = 1000)
    private String surgicalProcedures;

    @Column(name = "treatment_authorization_code", length = 50)
    private String treatmentAuthorizationCode;

    // Physician
    @Column(name = "physician_name", length = 100)
    private String physicianName; // Can be null initially, set from patient or OASIS

    @Column(name = "physician_npi", length = 10)
    private String physicianNpi;

    @Column(name = "physician_phone", length = 20)
    private String physicianPhone;

    @Column(name = "physician_orders_date")
    private LocalDate physicianOrdersDate;

    // Care Team
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_manager_id")
    private User caseManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_nurse_id")
    private User primaryNurse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_therapist_id")
    private User primaryTherapist;

    // Status
    @Column(name = "status", nullable = false, length = 20)
    private String status; // ACTIVE, PENDING, COMPLETED, CANCELLED, ON_HOLD

    @Column(name = "status_reason", length = 500)
    private String statusReason;

    // Insurance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_insurance_id")
    private Insurance primaryInsurance;

    // Financial
    @Column(name = "expected_frequency", length = 100)
    private String expectedFrequency; // e.g., "RN: 3W3, PT: 2W3"

    @Column(name = "total_authorized_visits")
    private Integer totalAuthorizedVisits;

    @Column(name = "visits_completed")
    private Integer visitsCompleted = 0;

    // Admission Source
    @Column(name = "admission_source", length = 100)
    private String admissionSource; // HOSPITAL, SNF, PHYSICIAN, SELF

    @Column(name = "admission_source_facility", length = 200)
    private String admissionSourceFacility;

    // Discharge Information
    @Column(name = "discharge_reason", length = 500)
    private String dischargeReason;

    @Column(name = "discharge_disposition", length = 100)
    private String dischargeDisposition;

    // Notes
    @Column(name = "clinical_notes", length = 2000)
    private String clinicalNotes;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    // Flags
    @Column(name = "is_homebound")
    private Boolean isHomebound = true;

    @Column(name = "requires_skilled_nursing")
    private Boolean requiresSkilledNursing = false;

    @Column(name = "requires_therapy")
    private Boolean requiresTherapy = false;

    @Column(name = "requires_aide")
    private Boolean requiresAide = false;

    // Helper methods
    public Integer getRemainingDays() {
        if (certificationEndDate == null) return null;
        LocalDate now = LocalDate.now();
        if (now.isAfter(certificationEndDate)) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(now, certificationEndDate);
    }

    public Integer getRemainingVisits() {
        if (totalAuthorizedVisits == null) return null;
        return totalAuthorizedVisits - (visitsCompleted != null ? visitsCompleted : 0);
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isCertificationExpiringSoon() {
        Integer remaining = getRemainingDays();
        return remaining != null && remaining <= 14; // 2 weeks
    }

    public String getEpisodeTitle() {
        return episodeType + " - " + episodeNumber;
    }
}

