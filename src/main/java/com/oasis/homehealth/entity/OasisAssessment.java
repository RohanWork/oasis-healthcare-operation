package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "oasis_assessments", indexes = {
    @Index(name = "idx_oasis_patient", columnList = "patient_id"),
    @Index(name = "idx_oasis_episode", columnList = "episode_id"),
    @Index(name = "idx_oasis_status", columnList = "status"),
    @Index(name = "idx_oasis_type", columnList = "assessment_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OasisAssessment extends BaseEntity {

    // Relationships
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

    // Assessment Metadata
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

    // Section Completion Tracking (JSON or separate table)
    @Column(name = "section_completion", columnDefinition = "TEXT")
    private String sectionCompletion; // JSON: {"section1": true, "section2": false}

    // Skip Logic Tracking
    @Column(name = "skipped_fields", columnDefinition = "TEXT")
    private String skippedFields; // JSON: ["M1005", "M1307", ...]

    // ============================================
    // SECTION 1: Patient Tracking & Administrative
    // ============================================
    
    @Column(name = "m0010_cms_cert_number", length = 12)
    private String m0010CmsCertNumber;

    @Column(name = "m0014_branch_state", length = 2)
    private String m0014BranchState;

    @Column(name = "m0016_branch_id", length = 10)
    private String m0016BranchId;

    @Column(name = "m0018_npi", length = 10)
    private String m0018Npi;

    @Column(name = "m0020_patient_id", length = 20)
    private String m0020PatientId;

    @Column(name = "m0030_soc_date")
    private LocalDate m0030SocDate;

    @Column(name = "m0032_roc_date")
    private LocalDate m0032RocDate;

    @Column(name = "m0063_medicare_number", length = 20)
    private String m0063MedicareNumber;

    @Column(name = "m0064_ssn", length = 11)
    private String m0064Ssn;

    @Column(name = "m0065_medicaid_number", length = 20)
    private String m0065MedicaidNumber;

    @Column(name = "m0069_gender", length = 1)
    private String m0069Gender; // M, F

    @Column(name = "m0140_race_ethnicity", length = 50)
    private String m0140RaceEthnicity;

    // ============================================
    // SECTION 2: Patient History & Diagnoses
    // ============================================
    
    @Column(name = "m1000_inpatient_facility", length = 2)
    private String m1000InpatientFacility; // 1-7, NA, UK

    @Column(name = "m1005_inpatient_discharge_date")
    private LocalDate m1005InpatientDischargeDate; // SKIP if M1000 = NA

    @Column(name = "m1011_inpatient_diagnosis", length = 500)
    private String m1011InpatientDiagnosis;

    @Column(name = "m1017_diagnosis_change", length = 500)
    private String m1017DiagnosisChange;

    @Column(name = "m1021_primary_diagnosis_icd", length = 10)
    private String m1021PrimaryDiagnosisIcd;

    @Column(name = "m1021_primary_diagnosis_desc", length = 500)
    private String m1021PrimaryDiagnosisDesc;

    @Column(name = "m1023_other_diagnosis1_icd", length = 10)
    private String m1023OtherDiagnosis1Icd;

    @Column(name = "m1023_other_diagnosis2_icd", length = 10)
    private String m1023OtherDiagnosis2Icd;

    @Column(name = "m1023_other_diagnosis3_icd", length = 10)
    private String m1023OtherDiagnosis3Icd;

    @Column(name = "m1023_other_diagnosis4_icd", length = 10)
    private String m1023OtherDiagnosis4Icd;

    @Column(name = "m1023_other_diagnosis5_icd", length = 10)
    private String m1023OtherDiagnosis5Icd;

    @Column(name = "m1028_active_diagnoses", columnDefinition = "TEXT")
    private String m1028ActiveDiagnoses; // Checkboxes: multiple selections

    // ============================================
    // SECTION 3: Living Arrangements
    // ============================================
    
    @Column(name = "m1100_living_situation", length = 2)
    private String m1100LivingSituation; // 01-11

    // ============================================
    // SECTION 4: Sensory Status
    // ============================================
    
    @Column(name = "m1200_vision", length = 1)
    private String m1200Vision; // 0-2

    @Column(name = "m1242_hearing", length = 1)
    private String m1242Hearing; // 0-4

    // ============================================
    // SECTION 5: Integumentary Status
    // ============================================
    
    @Column(name = "m1306_pressure_ulcer", length = 1)
    private String m1306PressureUlcer; // 0, 1

    @Column(name = "m1307_oldest_stage2_date")
    private LocalDate m1307OldestStage2Date; // SKIP if M1306 = 0

    @Column(name = "m1308_stage1_count")
    private Integer m1308Stage1Count; // SKIP if M1306 = 0

    @Column(name = "m1308_stage2_count")
    private Integer m1308Stage2Count;

    @Column(name = "m1308_stage3_count")
    private Integer m1308Stage3Count;

    @Column(name = "m1308_stage4_count")
    private Integer m1308Stage4Count;

    @Column(name = "m1308_unstageable_count")
    private Integer m1308UnstageableCount;

    @Column(name = "m1320_pressure_ulcer_status", length = 2)
    private String m1320PressureUlcerStatus; // SKIP if M1306 = 0

    @Column(name = "m1330_stasis_ulcer", length = 1)
    private String m1330StasisUlcer; // 0, 1

    @Column(name = "m1332_stasis_ulcer_count")
    private Integer m1332StasisUlcerCount; // SKIP if M1330 = 0

    @Column(name = "m1334_stasis_ulcer_status", length = 2)
    private String m1334StasisUlcerStatus; // SKIP if M1330 = 0

    @Column(name = "m1340_surgical_wound", length = 1)
    private String m1340SurgicalWound; // 0, 1

    @Column(name = "m1342_surgical_wound_status", length = 2)
    private String m1342SurgicalWoundStatus; // SKIP if M1340 = 0

    // ============================================
    // SECTION 6: Respiratory Status
    // ============================================
    
    @Column(name = "m1400_dyspnea", length = 1)
    private String m1400Dyspnea; // 0-4

    @Column(name = "m1410_respiratory_treatments", length = 100)
    private String m1410RespiratoryTreatments; // Multiple checkboxes

    // ============================================
    // SECTION 7: Cardiac Status
    // ============================================
    
    @Column(name = "m1500_heart_failure_symptoms", length = 100)
    private String m1500HeartFailureSymptoms; // Multiple checkboxes

    // ============================================
    // SECTION 8: Elimination Status
    // ============================================
    
    @Column(name = "m1600_uti_treatment", length = 1)
    private String m1600UtiTreatment; // 0, 1, NA

    @Column(name = "m1610_urinary_incontinence", length = 1)
    private String m1610UrinaryIncontinence; // 0-4

    @Column(name = "m1615_incontinence_timing", length = 1)
    private String m1615IncontinenceTiming; // SKIP if M1610 = 0

    @Column(name = "m1620_bowel_incontinence", length = 1)
    private String m1620BowelIncontinence; // 0-5

    @Column(name = "m1630_ostomy", length = 1)
    private String m1630Ostomy; // 0, 1

    // ============================================
    // SECTION 9: Neurological/Emotional/Behavioral
    // ============================================
    
    @Column(name = "m1700_cognitive_functioning", length = 1)
    private String m1700CognitiveFunctioning; // 0-4

    @Column(name = "m1710_when_confused", length = 1)
    private String m1710WhenConfused; // 0-4

    @Column(name = "m1720_when_anxious", length = 1)
    private String m1720WhenAnxious; // 0-3

    @Column(name = "m1730_depression_screening", length = 1)
    private String m1730DepressionScreening; // 0, 1

    @Column(name = "m1740_psychiatric_symptoms", length = 100)
    private String m1740PsychiatricSymptoms; // Multiple checkboxes

    @Column(name = "m1745_disruptive_behavior_freq", length = 1)
    private String m1745DisruptiveBehaviorFreq; // 0-3

    // ============================================
    // SECTION 10: Functional Abilities (GG Items)
    // ============================================
    
    // GG0100: Prior Functioning
    @Column(name = "gg0100_prior_functioning", columnDefinition = "TEXT")
    private String gg0100PriorFunctioning; // JSON for multiple items

    // GG0110: Prior Device Use
    @Column(name = "gg0110_prior_device_use", columnDefinition = "TEXT")
    private String gg0110PriorDeviceUse;

    // GG0130: Self-Care (Eating, Oral Hygiene, Toileting, Shower/Bathe, Dressing)
    @Column(name = "gg0130_self_care", columnDefinition = "TEXT")
    private String gg0130SelfCare; // JSON for all self-care items

    // GG0170: Mobility (Roll, Sit to Lying, Transfer, Walk, Stairs, etc.)
    @Column(name = "gg0170_mobility", columnDefinition = "TEXT")
    private String gg0170Mobility; // JSON for all mobility items

    // ============================================
    // SECTION 11: Medications
    // ============================================
    
    @Column(name = "m2001_drug_regimen_review", length = 1)
    private String m2001DrugRegimenReview; // 0, 1, NA

    @Column(name = "m2003_medication_followup", length = 1)
    private String m2003MedicationFollowup; // 0, 1, NA

    @Column(name = "m2005_medication_intervention", length = 1)
    private String m2005MedicationIntervention; // 0, 1, NA

    @Column(name = "m2010_high_risk_drug_education", length = 1)
    private String m2010HighRiskDrugEducation; // 0, 1, NA

    @Column(name = "m2015_drug_education_intervention", length = 1)
    private String m2015DrugEducationIntervention; // 0, 1, NA

    @Column(name = "m2020_oral_medication_management", length = 1)
    private String m2020OralMedicationManagement; // 0-3

    @Column(name = "m2030_injectable_medication_mgmt", length = 1)
    private String m2030InjectableMedicationMgmt; // 0-2, NA

    @Column(name = "m2040_prior_medication_mgmt", length = 1)
    private String m2040PriorMedicationMgmt; // 0-3

    // ============================================
    // SECTION 12: Care Management
    // ============================================
    
    @Column(name = "m2102_assistance_types", columnDefinition = "TEXT")
    private String m2102AssistanceTypes; // JSON for multiple selections

    @Column(name = "m2110_assistance_frequency", length = 1)
    private String m2110AssistanceFrequency; // 0-6

    // ============================================
    // SECTION 13: Emergent Care
    // ============================================
    
    @Column(name = "m2310_emergent_care", length = 1)
    private String m2310EmergentCare; // 0-9

    @Column(name = "m2410_emergent_care_reason", length = 500)
    private String m2410EmergentCareReason; // SKIP if M2310 = 0

    // ============================================
    // SECTION 14: COVID-19 Vaccination (NEW in E1)
    // ============================================
    
    @Column(name = "o0350_covid_vaccination", length = 1)
    private String o0350CovidVaccination; // 0, 1, 2, 9

    // ============================================
    // SECTION 15: Patient Mood (PHQ-2 to PHQ-9)
    // ============================================
    
    @Column(name = "d0150_phq2_interest", length = 1)
    private String d0150Phq2Interest; // 0-3

    @Column(name = "d0150_phq2_depressed", length = 1)
    private String d0150Phq2Depressed; // 0-3

    // PHQ-9 items (SKIP if PHQ-2 score < 3)
    @Column(name = "d0150_phq9_q3", length = 1)
    private String d0150Phq9Q3;

    @Column(name = "d0150_phq9_q4", length = 1)
    private String d0150Phq9Q4;

    @Column(name = "d0150_phq9_q5", length = 1)
    private String d0150Phq9Q5;

    @Column(name = "d0150_phq9_q6", length = 1)
    private String d0150Phq9Q6;

    @Column(name = "d0150_phq9_q7", length = 1)
    private String d0150Phq9Q7;

    @Column(name = "d0150_phq9_q8", length = 1)
    private String d0150Phq9Q8;

    @Column(name = "d0150_phq9_q9", length = 1)
    private String d0150Phq9Q9;

    @Column(name = "d0160_phq9_total_score")
    private Integer d0160Phq9TotalScore;

    // ============================================
    // SECTION 16: Immunization
    // ============================================
    
    @Column(name = "m1041_influenza_vaccine_period", length = 1)
    private String m1041InfluenzaVaccinePeriod; // 0, 1

    @Column(name = "m1046_influenza_vaccine_received", length = 1)
    private String m1046InfluenzaVaccineReceived; // 0, 1

    @Column(name = "m1051_pneumococcal_vaccine", length = 1)
    private String m1051PneumococcalVaccine; // 0, 1, 2

    // ============================================
    // SECTION 17: Discharge Planning
    // ============================================
    
    @Column(name = "m2401_intervention_synopsis", columnDefinition = "TEXT")
    private String m2401InterventionSynopsis; // Multiple checkboxes

    @Column(name = "m2410_discharge_to", length = 2)
    private String m2410DischargeTo; // 01-09, UK

    @Column(name = "m2420_discharge_disposition", length = 2)
    private String m2420DischargeDisposition; // 01-09

    // Helper methods
    public boolean isLocked() {
        return "LOCKED".equals(status);
    }

    public boolean canEdit() {
        return "DRAFT".equals(status) || "REJECTED".equals(status);
    }

    public boolean needsQAReview() {
        return "SUBMITTED".equals(status);
    }

    public Integer calculatePhq2Score() {
        if (d0150Phq2Interest == null || d0150Phq2Depressed == null) return null;
        try {
            return Integer.parseInt(d0150Phq2Interest) + Integer.parseInt(d0150Phq2Depressed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean shouldSkipPhq9() {
        Integer phq2Score = calculatePhq2Score();
        return phq2Score != null && phq2Score < 3;
    }
}

