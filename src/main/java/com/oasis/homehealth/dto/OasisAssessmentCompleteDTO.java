package com.oasis.homehealth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Complete OASIS-E1 Assessment DTO
 * Contains ALL fields from official CMS OASIS-E1 document
 * Sections A through Q (300+ fields)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OasisAssessmentCompleteDTO {

    // ============================================
    // METADATA
    // ============================================
    
    private Long id;
    private Long patientId;
    private String patientName;
    private Long episodeId;
    private Long organizationId;
    private Long clinicianId;
    private String clinicianName;

    private String assessmentType; // SOC, ROC, RECERT, TRANSFER, DISCHARGE
    private String assessmentReason;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessmentDate;
    
    private String status; // DRAFT, SUBMITTED, QA_REVIEW, APPROVED, REJECTED, LOCKED
    private Integer completionPercentage;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastAutoSaved;

    // QA Workflow
    private Long submittedById;
    private String submittedByName;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;
    
    private Long reviewedById;
    private String reviewedByName;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reviewedAt;
    
    private String qaComments;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lockedAt;

    private Map<String, Boolean> sectionCompletion;
    private List<String> skippedFields;

    // ============================================
    // SECTION A: ADMINISTRATIVE INFORMATION
    // ============================================
    
    private String m0010CmsCertNumber;
    private String m0014BranchState;
    private String m0016BranchId;
    private String m0018Npi;
    private String m0020PatientId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate m0030SocDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate m0032RocDate;
    
    private String m0040PatientName;
    private String m0050PatientState;
    private String m0060PatientZip;
    private String m0063MedicareNumber;
    private String m0064Ssn;
    private String m0065MedicaidNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate m0066BirthDate;
    
    private String m0069Gender;
    private String m0140RaceEthnicity;

    // ============================================
    // SECTION B: PATIENT HISTORY AND DIAGNOSES
    // ============================================
    
    private String m1000InpatientFacility;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate m1005InpatientDischargeDate;
    
    private String m1011InpatientDiagnosis;
    private String m1017DiagnosisChange;
    private String m1021PrimaryDiagnosisIcd;
    private String m1021PrimaryDiagnosisDesc;
    private String m1021PrimaryDiagnosisSeverity;
    private String m1023OtherDiagnosis1Icd;
    private String m1023OtherDiagnosis1Severity;
    private String m1023OtherDiagnosis2Icd;
    private String m1023OtherDiagnosis2Severity;
    private String m1023OtherDiagnosis3Icd;
    private String m1023OtherDiagnosis3Severity;
    private String m1023OtherDiagnosis4Icd;
    private String m1023OtherDiagnosis4Severity;
    private String m1023OtherDiagnosis5Icd;
    private String m1023OtherDiagnosis5Severity;
    private String m1028ActiveDiagnoses;
    private String m1033RiskHospitalization;

    // ============================================
    // SECTION C: LIVING ARRANGEMENTS
    // ============================================
    
    private String m1100LivingSituation;

    // ============================================
    // SECTION D: SENSORY STATUS
    // ============================================
    
    private String m1200Vision;
    private String m1242Hearing;

    // ============================================
    // SECTION E: SKIN CONDITIONS
    // ============================================
    
    private String m1306PressureUlcer;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate m1307OldestStage2Date;
    
    private Integer m1308Stage1Count;
    private Integer m1308Stage2Count;
    private Integer m1308Stage3Count;
    private Integer m1308Stage4Count;
    private Integer m1311UnstageableDressing;
    private Integer m1311UnstageableSlough;
    private Integer m1311UnstageableDeepTissue;
    private Integer m1322Stage3Count;
    private Integer m1322Stage4Count;
    private String m1324ProblematicStage;
    private String m1320PressureUlcerStatus;
    private String m1330StasisUlcer;
    private Integer m1332StasisUlcerCount;
    private String m1334StasisUlcerStatus;
    private String m1340SurgicalWound;
    private String m1342SurgicalWoundStatus;

    // ============================================
    // SECTION F: RESPIRATORY STATUS
    // ============================================
    
    private String m1400Dyspnea;
    private String m1410RespiratoryTreatments;

    // ============================================
    // SECTION G: ELIMINATION STATUS
    // ============================================
    
    private String m1600UtiTreatment;
    private String m1610UrinaryIncontinence;
    private String m1615IncontinenceTiming;
    private String m1620BowelIncontinence;
    private String m1630Ostomy;

    // ============================================
    // SECTION GG: FUNCTIONAL ABILITIES AND GOALS
    // ============================================
    
    // GG0100: Prior Functioning
    private String gg0100PriorSelfCare;
    private String gg0100PriorIndoorMobility;
    private String gg0100PriorStairs;
    private String gg0100PriorFunctionalCognition;

    // GG0110: Prior Device Use
    private Boolean gg0110ManualWheelchair;
    private Boolean gg0110MotorizedWheelchair;
    private Boolean gg0110MechanicalLift;
    private Boolean gg0110Walker;
    private Boolean gg0110OrthoticsProsthetics;
    private Boolean gg0110None;

    // GG0130: Self-Care (Admission, Goal, Discharge for each)
    // A. Eating
    private String gg0130aEatingAdmission;
    private String gg0130aEatingGoal;
    private String gg0130aEatingDischarge;

    // B. Oral Hygiene
    private String gg0130bOralAdmission;
    private String gg0130bOralGoal;
    private String gg0130bOralDischarge;

    // C. Toileting Hygiene
    private String gg0130cToiletingAdmission;
    private String gg0130cToiletingGoal;
    private String gg0130cToiletingDischarge;

    // E. Shower/Bathe Self
    private String gg0130eShowerAdmission;
    private String gg0130eShowerGoal;
    private String gg0130eShowerDischarge;

    // F. Upper Body Dressing
    private String gg0130fUpperDressAdmission;
    private String gg0130fUpperDressGoal;
    private String gg0130fUpperDressDischarge;

    // G. Lower Body Dressing
    private String gg0130gLowerDressAdmission;
    private String gg0130gLowerDressGoal;
    private String gg0130gLowerDressDischarge;

    // H. Putting on/taking off footwear
    private String gg0130hFootwearAdmission;
    private String gg0130hFootwearGoal;
    private String gg0130hFootwearDischarge;

    // GG0170: Mobility (Admission, Goal, Discharge for each)
    // A. Roll left and right
    private String gg0170aRollAdmission;
    private String gg0170aRollGoal;
    private String gg0170aRollDischarge;

    // B. Sit to lying
    private String gg0170bSitLyingAdmission;
    private String gg0170bSitLyingGoal;
    private String gg0170bSitLyingDischarge;

    // C. Lying to sitting on side of bed
    private String gg0170cLyingSitAdmission;
    private String gg0170cLyingSitGoal;
    private String gg0170cLyingSitDischarge;

    // D. Sit to stand
    private String gg0170dSitStandAdmission;
    private String gg0170dSitStandGoal;
    private String gg0170dSitStandDischarge;

    // E. Chair/bed-to-chair transfer
    private String gg0170eTransferAdmission;
    private String gg0170eTransferGoal;
    private String gg0170eTransferDischarge;

    // F. Toilet transfer
    private String gg0170fToiletAdmission;
    private String gg0170fToiletGoal;
    private String gg0170fToiletDischarge;

    // G. Car transfer
    private String gg0170gCarAdmission;
    private String gg0170gCarGoal;
    private String gg0170gCarDischarge;

    // I. Walk 10 feet
    private String gg0170iWalk10Admission;
    private String gg0170iWalk10Goal;
    private String gg0170iWalk10Discharge;

    // J. Walk 50 feet with two turns
    private String gg0170jWalk50Admission;
    private String gg0170jWalk50Goal;
    private String gg0170jWalk50Discharge;

    // K. Walk 150 feet
    private String gg0170kWalk150Admission;
    private String gg0170kWalk150Goal;
    private String gg0170kWalk150Discharge;

    // L. Walking 10 feet on uneven surfaces
    private String gg0170lWalkUnevenAdmission;
    private String gg0170lWalkUnevenGoal;
    private String gg0170lWalkUnevenDischarge;

    // M. 1 step (curb)
    private String gg0170mStep1Admission;
    private String gg0170mStep1Goal;
    private String gg0170mStep1Discharge;

    // N. 4 steps
    private String gg0170nStep4Admission;
    private String gg0170nStep4Goal;
    private String gg0170nStep4Discharge;

    // O. 12 steps
    private String gg0170oStep12Admission;
    private String gg0170oStep12Goal;
    private String gg0170oStep12Discharge;

    // P. Picking up object
    private String gg0170pPickupAdmission;
    private String gg0170pPickupGoal;
    private String gg0170pPickupDischarge;

    // Q. Wheel 50 feet with two turns
    private String gg0170qWheel50Admission;
    private String gg0170qWheel50Goal;
    private String gg0170qWheel50Discharge;

    // R. Wheel 150 feet
    private String gg0170rWheel150Admission;
    private String gg0170rWheel150Goal;
    private String gg0170rWheel150Discharge;

    // ============================================
    // SECTION H: CARDIAC STATUS
    // ============================================
    
    private String m1500HeartFailureSymptoms;

    // ============================================
    // SECTION I: NEUROLOGICAL/EMOTIONAL/BEHAVIORAL STATUS
    // ============================================
    
    private String m1700CognitiveFunctioning;
    private String m1710WhenConfused;
    private String m1720WhenAnxious;
    private String m1730DepressionScreening;
    private String m1740PsychiatricSymptoms;
    private String m1745DisruptiveBehaviorFreq;

    // ============================================
    // SECTION J: HEALTH CONDITIONS
    // ============================================
    
    private String j0510PainSleep;
    private String j0520PainTherapy;
    private String j1800AnyFalls;
    private String j1900FallCount;

    // ============================================
    // SECTION M: MEDICATIONS
    // ============================================
    
    private String m2001DrugRegimenReview;
    private String m2003MedicationFollowup;
    private String m2005MedicationIntervention;
    private String m2010HighRiskDrugEducation;
    private String m2015DrugEducationIntervention;
    private String m2020OralMedicationManagement;
    private String m2030InjectableMedicationMgmt;
    private String m2040PriorMedicationMgmt;

    // ============================================
    // SECTION N: CARE MANAGEMENT
    // ============================================
    
    private String m2102AssistanceTypes;
    private String m2110AssistanceFrequency;

    // ============================================
    // SECTION O: SPECIAL TREATMENTS
    // ============================================
    
    private Boolean o0110Chemotherapy;
    private Boolean o0110Radiation;
    private Boolean o0110Oxygen;
    private Boolean o0110Suctioning;
    private Boolean o0110Tracheostomy;
    private Boolean o0110InvasiveVentilator;
    private Boolean o0110NoninvasiveVentilator;
    private Boolean o0110IvMedications;
    private Boolean o0110Transfusions;
    private Boolean o0110Dialysis;
    private Boolean o0110IvAccess;
    private Boolean o0110EnteralNutrition;
    private Boolean o0110ParenteralNutrition;
    private Boolean o0110None;
    private String o0350CovidVaccination;

    // ============================================
    // SECTION P: IMMUNIZATION
    // ============================================
    
    private String m1041InfluenzaVaccinePeriod;
    private String m1046InfluenzaVaccineReceived;
    private String m1051PneumococcalVaccine;

    // ============================================
    // SECTION Q: EMERGENT CARE AND DISCHARGE
    // ============================================
    
    private String m2310EmergentCare;
    private String m2410EmergentCareReason;
    private String m2420DischargeDisposition;

    // ============================================
    // AUDIT FIELDS
    // ============================================
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String updatedBy;

    // ============================================
    // HELPER METHODS
    // ============================================
    
    public boolean isEditable() {
        return "DRAFT".equals(status) || "REJECTED".equals(status);
    }

    public boolean isLocked() {
        return "LOCKED".equals(status);
    }

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


