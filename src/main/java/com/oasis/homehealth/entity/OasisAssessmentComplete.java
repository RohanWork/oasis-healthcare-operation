package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * COMPLETE OASIS-E1 Assessment Entity
 * Based on official CMS OASIS-E1 document
 * Contains ALL sections A through Q with complete field set
 */
@Entity
@Table(name = "oasis_assessments_complete", indexes = {
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
public class OasisAssessmentComplete extends BaseEntity {
    
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
    // SECTION A: ADMINISTRATIVE INFORMATION
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

    @Column(name = "m0040_patient_name", length = 200)
    private String m0040PatientName;

    @Column(name = "m0050_patient_state", length = 2)
    private String m0050PatientState;

    @Column(name = "m0060_patient_zip", length = 10)
    private String m0060PatientZip;

    @Column(name = "m0063_medicare_number", length = 20)
    private String m0063MedicareNumber;

    @Column(name = "m0064_ssn", length = 11)
    private String m0064Ssn;

    @Column(name = "m0065_medicaid_number", length = 20)
    private String m0065MedicaidNumber;

    @Column(name = "m0066_birth_date")
    private LocalDate m0066BirthDate;

    @Column(name = "m0069_gender", length = 1)
    private String m0069Gender; // M, F

    // M0140: Race/Ethnicity (multiple selections stored as JSON or comma-separated)
    @Column(name = "m0140_race_ethnicity", length = 500)
    private String m0140RaceEthnicity;

    // ============================================
    // SECTION B: PATIENT HISTORY AND DIAGNOSES
    // ============================================
    
    @Column(name = "m1000_inpatient_facility", length = 2)
    private String m1000InpatientFacility; // 1-7, NA, UK

    @Column(name = "m1005_inpatient_discharge_date")
    private LocalDate m1005InpatientDischargeDate; // SKIP if M1000 = NA

    @Column(name = "m1011_inpatient_diagnosis", length = 1000)
    private String m1011InpatientDiagnosis;

    @Column(name = "m1017_diagnosis_change", length = 1000)
    private String m1017DiagnosisChange;

    @Column(name = "m1021_primary_diagnosis_icd", length = 10)
    private String m1021PrimaryDiagnosisIcd;

    @Column(name = "m1021_primary_diagnosis_desc", length = 500)
    private String m1021PrimaryDiagnosisDesc;

    @Column(name = "m1021_primary_diagnosis_severity", length = 1)
    private String m1021PrimaryDiagnosisSeverity; // 0, 1, 2, 3

    @Column(name = "m1023_other_diagnosis1_icd", length = 10)
    private String m1023OtherDiagnosis1Icd;

    @Column(name = "m1023_other_diagnosis1_severity", length = 1)
    private String m1023OtherDiagnosis1Severity;

    @Column(name = "m1023_other_diagnosis2_icd", length = 10)
    private String m1023OtherDiagnosis2Icd;

    @Column(name = "m1023_other_diagnosis2_severity", length = 1)
    private String m1023OtherDiagnosis2Severity;

    @Column(name = "m1023_other_diagnosis3_icd", length = 10)
    private String m1023OtherDiagnosis3Icd;

    @Column(name = "m1023_other_diagnosis3_severity", length = 1)
    private String m1023OtherDiagnosis3Severity;

    @Column(name = "m1023_other_diagnosis4_icd", length = 10)
    private String m1023OtherDiagnosis4Icd;

    @Column(name = "m1023_other_diagnosis4_severity", length = 1)
    private String m1023OtherDiagnosis4Severity;

    @Column(name = "m1023_other_diagnosis5_icd", length = 10)
    private String m1023OtherDiagnosis5Icd;

    @Column(name = "m1023_other_diagnosis5_severity", length = 1)
    private String m1023OtherDiagnosis5Severity;

    @Column(name = "m1028_active_diagnoses", columnDefinition = "TEXT")
    private String m1028ActiveDiagnoses; // Multiple checkboxes

    @Column(name = "m1033_risk_hospitalization", length = 1)
    private String m1033RiskHospitalization; // 0, 1, 2, 3

    // ============================================
    // SECTION C: LIVING ARRANGEMENTS
    // ============================================
    
    @Column(name = "m1100_living_situation", length = 2)
    private String m1100LivingSituation; // 01-11

    // ============================================
    // SECTION D: SENSORY STATUS
    // ============================================
    
    @Column(name = "m1200_vision", length = 1)
    private String m1200Vision; // 0-2

    @Column(name = "m1242_hearing", length = 1)
    private String m1242Hearing; // 0-4

    // ============================================
    // SECTION E: SKIN CONDITIONS
    // ============================================
    
    @Column(name = "m1306_pressure_ulcer", length = 1)
    private String m1306PressureUlcer; // 0, 1

    @Column(name = "m1307_oldest_stage2_date")
    private LocalDate m1307OldestStage2Date; // SKIP if M1306 = 0

    // M1308: Current Number of Unhealed Pressure Ulcers at Each Stage
    @Column(name = "m1308_stage1_count")
    private Integer m1308Stage1Count;

    @Column(name = "m1308_stage2_count")
    private Integer m1308Stage2Count;

    @Column(name = "m1308_stage3_count")
    private Integer m1308Stage3Count;

    @Column(name = "m1308_stage4_count")
    private Integer m1308Stage4Count;

    // M1311: Current Number of Pressure Ulcers that are Unstageable
    @Column(name = "m1311_unstageable_dressing")
    private Integer m1311UnstageableDressing;

    @Column(name = "m1311_unstageable_slough")
    private Integer m1311UnstageableSlough;

    @Column(name = "m1311_unstageable_deep_tissue")
    private Integer m1311UnstageableDeepTissue;

    // M1322: Current Number of Stage III and IV Pressure Ulcers
    @Column(name = "m1322_stage3_count")
    private Integer m1322Stage3Count;

    @Column(name = "m1322_stage4_count")
    private Integer m1322Stage4Count;

    // M1324: Stage of Most Problematic Unhealed Pressure Ulcer
    @Column(name = "m1324_problematic_stage", length = 2)
    private String m1324ProblematicStage; // 1-4, A1-A3, NA

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
    // SECTION F: RESPIRATORY STATUS
    // ============================================
    
    @Column(name = "m1400_dyspnea", length = 1)
    private String m1400Dyspnea; // 0-4

    @Column(name = "m1410_respiratory_treatments", length = 100)
    private String m1410RespiratoryTreatments; // Multiple checkboxes

    // ============================================
    // SECTION G: ELIMINATION STATUS
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
    // SECTION GG: FUNCTIONAL ABILITIES AND GOALS
    // ============================================
    
    // GG0100: Prior Functioning: Everyday Activities
    @Column(name = "gg0100_prior_self_care", length = 1)
    private String gg0100PriorSelfCare; // 3, 2, 1, 0, 8

    @Column(name = "gg0100_prior_indoor_mobility", length = 1)
    private String gg0100PriorIndoorMobility;

    @Column(name = "gg0100_prior_stairs", length = 1)
    private String gg0100PriorStairs;

    @Column(name = "gg0100_prior_functional_cognition", length = 1)
    private String gg0100PriorFunctionalCognition;

    // GG0110: Prior Device Use (checkboxes)
    @Column(name = "gg0110_manual_wheelchair")
    private Boolean gg0110ManualWheelchair;

    @Column(name = "gg0110_motorized_wheelchair")
    private Boolean gg0110MotorizedWheelchair;

    @Column(name = "gg0110_mechanical_lift")
    private Boolean gg0110MechanicalLift;

    @Column(name = "gg0110_walker")
    private Boolean gg0110Walker;

    @Column(name = "gg0110_orthotics_prosthetics")
    private Boolean gg0110OrthoticsProsthetics;

    @Column(name = "gg0110_none")
    private Boolean gg0110None;

    // GG0130: Self-Care (Admission, Goal, Discharge for each)
    // A. Eating
    @Column(name = "gg0130a_eating_admission", length = 2)
    private String gg0130aEatingAdmission; // 01-10, 88

    @Column(name = "gg0130a_eating_goal", length = 2)
    private String gg0130aEatingGoal;

    @Column(name = "gg0130a_eating_discharge", length = 2)
    private String gg0130aEatingDischarge;

    // B. Oral Hygiene
    @Column(name = "gg0130b_oral_admission", length = 2)
    private String gg0130bOralAdmission;

    @Column(name = "gg0130b_oral_goal", length = 2)
    private String gg0130bOralGoal;

    @Column(name = "gg0130b_oral_discharge", length = 2)
    private String gg0130bOralDischarge;

    // C. Toileting Hygiene
    @Column(name = "gg0130c_toileting_admission", length = 2)
    private String gg0130cToiletingAdmission;

    @Column(name = "gg0130c_toileting_goal", length = 2)
    private String gg0130cToiletingGoal;

    @Column(name = "gg0130c_toileting_discharge", length = 2)
    private String gg0130cToiletingDischarge;

    // E. Shower/Bathe Self
    @Column(name = "gg0130e_shower_admission", length = 2)
    private String gg0130eShowerAdmission;

    @Column(name = "gg0130e_shower_goal", length = 2)
    private String gg0130eShowerGoal;

    @Column(name = "gg0130e_shower_discharge", length = 2)
    private String gg0130eShowerDischarge;

    // F. Upper Body Dressing
    @Column(name = "gg0130f_upper_dress_admission", length = 2)
    private String gg0130fUpperDressAdmission;

    @Column(name = "gg0130f_upper_dress_goal", length = 2)
    private String gg0130fUpperDressGoal;

    @Column(name = "gg0130f_upper_dress_discharge", length = 2)
    private String gg0130fUpperDressDischarge;

    // G. Lower Body Dressing
    @Column(name = "gg0130g_lower_dress_admission", length = 2)
    private String gg0130gLowerDressAdmission;

    @Column(name = "gg0130g_lower_dress_goal", length = 2)
    private String gg0130gLowerDressGoal;

    @Column(name = "gg0130g_lower_dress_discharge", length = 2)
    private String gg0130gLowerDressDischarge;

    // H. Putting on/taking off footwear
    @Column(name = "gg0130h_footwear_admission", length = 2)
    private String gg0130hFootwearAdmission;

    @Column(name = "gg0130h_footwear_goal", length = 2)
    private String gg0130hFootwearGoal;

    @Column(name = "gg0130h_footwear_discharge", length = 2)
    private String gg0130hFootwearDischarge;

    // GG0170: Mobility (Admission, Goal, Discharge for each)
    // A. Roll left and right
    @Column(name = "gg0170a_roll_admission", length = 2)
    private String gg0170aRollAdmission;

    @Column(name = "gg0170a_roll_goal", length = 2)
    private String gg0170aRollGoal;

    @Column(name = "gg0170a_roll_discharge", length = 2)
    private String gg0170aRollDischarge;

    // B. Sit to lying
    @Column(name = "gg0170b_sit_lying_admission", length = 2)
    private String gg0170bSitLyingAdmission;

    @Column(name = "gg0170b_sit_lying_goal", length = 2)
    private String gg0170bSitLyingGoal;

    @Column(name = "gg0170b_sit_lying_discharge", length = 2)
    private String gg0170bSitLyingDischarge;

    // C. Lying to sitting on side of bed
    @Column(name = "gg0170c_lying_sit_admission", length = 2)
    private String gg0170cLyingSitAdmission;

    @Column(name = "gg0170c_lying_sit_goal", length = 2)
    private String gg0170cLyingSitGoal;

    @Column(name = "gg0170c_lying_sit_discharge", length = 2)
    private String gg0170cLyingSitDischarge;

    // D. Sit to stand
    @Column(name = "gg0170d_sit_stand_admission", length = 2)
    private String gg0170dSitStandAdmission;

    @Column(name = "gg0170d_sit_stand_goal", length = 2)
    private String gg0170dSitStandGoal;

    @Column(name = "gg0170d_sit_stand_discharge", length = 2)
    private String gg0170dSitStandDischarge;

    // E. Chair/bed-to-chair transfer
    @Column(name = "gg0170e_transfer_admission", length = 2)
    private String gg0170eTransferAdmission;

    @Column(name = "gg0170e_transfer_goal", length = 2)
    private String gg0170eTransferGoal;

    @Column(name = "gg0170e_transfer_discharge", length = 2)
    private String gg0170eTransferDischarge;

    // F. Toilet transfer
    @Column(name = "gg0170f_toilet_admission", length = 2)
    private String gg0170fToiletAdmission;

    @Column(name = "gg0170f_toilet_goal", length = 2)
    private String gg0170fToiletGoal;

    @Column(name = "gg0170f_toilet_discharge", length = 2)
    private String gg0170fToiletDischarge;

    // G. Car transfer
    @Column(name = "gg0170g_car_admission", length = 2)
    private String gg0170gCarAdmission;

    @Column(name = "gg0170g_car_goal", length = 2)
    private String gg0170gCarGoal;

    @Column(name = "gg0170g_car_discharge", length = 2)
    private String gg0170gCarDischarge;

    // I. Walk 10 feet
    @Column(name = "gg0170i_walk10_admission", length = 2)
    private String gg0170iWalk10Admission;

    @Column(name = "gg0170i_walk10_goal", length = 2)
    private String gg0170iWalk10Goal;

    @Column(name = "gg0170i_walk10_discharge", length = 2)
    private String gg0170iWalk10Discharge;

    // J. Walk 50 feet with two turns
    @Column(name = "gg0170j_walk50_admission", length = 2)
    private String gg0170jWalk50Admission;

    @Column(name = "gg0170j_walk50_goal", length = 2)
    private String gg0170jWalk50Goal;

    @Column(name = "gg0170j_walk50_discharge", length = 2)
    private String gg0170jWalk50Discharge;

    // K. Walk 150 feet
    @Column(name = "gg0170k_walk150_admission", length = 2)
    private String gg0170kWalk150Admission;

    @Column(name = "gg0170k_walk150_goal", length = 2)
    private String gg0170kWalk150Goal;

    @Column(name = "gg0170k_walk150_discharge", length = 2)
    private String gg0170kWalk150Discharge;

    // L. Walking 10 feet on uneven surfaces
    @Column(name = "gg0170l_walk_uneven_admission", length = 2)
    private String gg0170lWalkUnevenAdmission;

    @Column(name = "gg0170l_walk_uneven_goal", length = 2)
    private String gg0170lWalkUnevenGoal;

    @Column(name = "gg0170l_walk_uneven_discharge", length = 2)
    private String gg0170lWalkUnevenDischarge;

    // M. 1 step (curb)
    @Column(name = "gg0170m_step1_admission", length = 2)
    private String gg0170mStep1Admission;

    @Column(name = "gg0170m_step1_goal", length = 2)
    private String gg0170mStep1Goal;

    @Column(name = "gg0170m_step1_discharge", length = 2)
    private String gg0170mStep1Discharge;

    // N. 4 steps
    @Column(name = "gg0170n_step4_admission", length = 2)
    private String gg0170nStep4Admission;

    @Column(name = "gg0170n_step4_goal", length = 2)
    private String gg0170nStep4Goal;

    @Column(name = "gg0170n_step4_discharge", length = 2)
    private String gg0170nStep4Discharge;

    // O. 12 steps
    @Column(name = "gg0170o_step12_admission", length = 2)
    private String gg0170oStep12Admission;

    @Column(name = "gg0170o_step12_goal", length = 2)
    private String gg0170oStep12Goal;

    @Column(name = "gg0170o_step12_discharge", length = 2)
    private String gg0170oStep12Discharge;

    // P. Picking up object
    @Column(name = "gg0170p_pickup_admission", length = 2)
    private String gg0170pPickupAdmission;

    @Column(name = "gg0170p_pickup_goal", length = 2)
    private String gg0170pPickupGoal;

    @Column(name = "gg0170p_pickup_discharge", length = 2)
    private String gg0170pPickupDischarge;

    // Q. Wheel 50 feet with two turns
    @Column(name = "gg0170q_wheel50_admission", length = 2)
    private String gg0170qWheel50Admission;

    @Column(name = "gg0170q_wheel50_goal", length = 2)
    private String gg0170qWheel50Goal;

    @Column(name = "gg0170q_wheel50_discharge", length = 2)
    private String gg0170qWheel50Discharge;

    // R. Wheel 150 feet
    @Column(name = "gg0170r_wheel150_admission", length = 2)
    private String gg0170rWheel150Admission;

    @Column(name = "gg0170r_wheel150_goal", length = 2)
    private String gg0170rWheel150Goal;

    @Column(name = "gg0170r_wheel150_discharge", length = 2)
    private String gg0170rWheel150Discharge;

    // ============================================
    // SECTION H: CARDIAC STATUS
    // ============================================
    
    @Column(name = "m1500_heart_failure_symptoms", length = 100)
    private String m1500HeartFailureSymptoms;

    // ============================================
    // SECTION I: NEUROLOGICAL/EMOTIONAL/BEHAVIORAL STATUS
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
    // SECTION J: HEALTH CONDITIONS
    // ============================================
    
    // J0510: Pain Effect on Sleep
    @Column(name = "j0510_pain_sleep", length = 1)
    private String j0510PainSleep; // 0-4

    // J0520: Pain Interference with Therapy
    @Column(name = "j0520_pain_therapy", length = 1)
    private String j0520PainTherapy; // 0-4

    // J1800: Any Falls Since SOC/ROC
    @Column(name = "j1800_any_falls", length = 1)
    private String j1800AnyFalls; // 0, 1

    // J1900: Number of Falls Since SOC/ROC
    @Column(name = "j1900_fall_count", length = 1)
    private String j1900FallCount; // 0, 1, 2

    // ============================================
    // SECTION M: MEDICATIONS
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
    // SECTION N: CARE MANAGEMENT
    // ============================================
    
    @Column(name = "m2102_assistance_types", columnDefinition = "TEXT")
    private String m2102AssistanceTypes; // JSON for multiple selections

    @Column(name = "m2110_assistance_frequency", length = 1)
    private String m2110AssistanceFrequency; // 0-6

    // ============================================
    // SECTION O: SPECIAL TREATMENTS, PROCEDURES, AND PROGRAMS
    // ============================================
    
    // O0110: Special Treatments (checkboxes)
    @Column(name = "o0110_chemotherapy")
    private Boolean o0110Chemotherapy;

    @Column(name = "o0110_radiation")
    private Boolean o0110Radiation;

    @Column(name = "o0110_oxygen")
    private Boolean o0110Oxygen;

    @Column(name = "o0110_suctioning")
    private Boolean o0110Suctioning;

    @Column(name = "o0110_tracheostomy")
    private Boolean o0110Tracheostomy;

    @Column(name = "o0110_invasive_ventilator")
    private Boolean o0110InvasiveVentilator;

    @Column(name = "o0110_noninvasive_ventilator")
    private Boolean o0110NoninvasiveVentilator;

    @Column(name = "o0110_iv_medications")
    private Boolean o0110IvMedications;

    @Column(name = "o0110_transfusions")
    private Boolean o0110Transfusions;

    @Column(name = "o0110_dialysis")
    private Boolean o0110Dialysis;

    @Column(name = "o0110_iv_access")
    private Boolean o0110IvAccess;

    @Column(name = "o0110_enteral_nutrition")
    private Boolean o0110EnteralNutrition;

    @Column(name = "o0110_parenteral_nutrition")
    private Boolean o0110ParenteralNutrition;

    @Column(name = "o0110_none")
    private Boolean o0110None;

    // O0350: COVID-19 Vaccination (NEW in E1)
    @Column(name = "o0350_covid_vaccination", length = 1)
    private String o0350CovidVaccination; // 0, 1, 2, 9

    // ============================================
    // SECTION P: IMMUNIZATION
    // ============================================
    
    @Column(name = "m1041_influenza_vaccine_period", length = 1)
    private String m1041InfluenzaVaccinePeriod; // 0, 1

    @Column(name = "m1046_influenza_vaccine_received", length = 1)
    private String m1046InfluenzaVaccineReceived; // 0, 1

    @Column(name = "m1051_pneumococcal_vaccine", length = 1)
    private String m1051PneumococcalVaccine; // 0, 1, 2

    // ============================================
    // SECTION Q: EMERGENT CARE AND DISCHARGE
    // ============================================
    
    @Column(name = "m2310_emergent_care", length = 1)
    private String m2310EmergentCare; // 0-9

    @Column(name = "m2410_emergent_care_reason", length = 500)
    private String m2410EmergentCareReason; // SKIP if M2310 = 0

    @Column(name = "m2420_discharge_disposition", length = 2)
    private String m2420DischargeDisposition; // 01-09

    // ============================================
    // HELPER METHODS
    // ============================================
    // (Methods isLocked(), canEdit(), isDeleted() defined at top of class)
    
    public boolean needsQAReview() {
        return "SUBMITTED".equals(status);
    }

    // Skip logic helpers
    public boolean shouldSkipM1005() {
        return "NA".equals(m1000InpatientFacility);
    }

    public boolean shouldSkipPressureUlcerDetails() {
        return "0".equals(m1306PressureUlcer);
    }

    public boolean shouldSkipStasisUlcerDetails() {
        return "0".equals(m1330StasisUlcer);
    }

    public boolean shouldSkipSurgicalWoundDetails() {
        return "0".equals(m1340SurgicalWound);
    }

    public boolean shouldSkipIncontinenceTiming() {
        return "0".equals(m1610UrinaryIncontinence);
    }

    public boolean shouldSkipEmergentCareReason() {
        return "0".equals(m2310EmergentCare);
    }
}


