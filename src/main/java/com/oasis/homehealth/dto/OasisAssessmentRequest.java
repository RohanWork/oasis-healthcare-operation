package com.oasis.homehealth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OasisAssessmentRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long episodeId;

    @NotNull(message = "Assessment type is required")
    private String assessmentType; // SOC, ROC, RECERT, TRANSFER, DISCHARGE

    private String assessmentReason;

    @NotNull(message = "Assessment date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessmentDate;

    private Long clinicianId;

    // Section Completion & Skip Logic
    private Map<String, Boolean> sectionCompletion;
    private List<String> skippedFields;

    // ============================================
    // SECTION 1: Patient Tracking & Administrative
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
    
    private String m0063MedicareNumber;
    private String m0064Ssn;
    private String m0065MedicaidNumber;
    private String m0069Gender;
    private String m0140RaceEthnicity;

    // ============================================
    // SECTION 2: Patient History & Diagnoses
    // ============================================
    
    private String m1000InpatientFacility;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate m1005InpatientDischargeDate;
    
    private String m1011InpatientDiagnosis;
    private String m1017DiagnosisChange;
    private String m1021PrimaryDiagnosisIcd;
    private String m1021PrimaryDiagnosisDesc;
    private String m1023OtherDiagnosis1Icd;
    private String m1023OtherDiagnosis2Icd;
    private String m1023OtherDiagnosis3Icd;
    private String m1023OtherDiagnosis4Icd;
    private String m1023OtherDiagnosis5Icd;
    private String m1028ActiveDiagnoses;

    // ============================================
    // SECTION 3: Living Arrangements
    // ============================================
    
    private String m1100LivingSituation;

    // ============================================
    // SECTION 4: Sensory Status
    // ============================================
    
    private String m1200Vision;
    private String m1242Hearing;

    // ============================================
    // SECTION 5: Integumentary Status
    // ============================================
    
    private String m1306PressureUlcer;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate m1307OldestStage2Date;
    
    private Integer m1308Stage1Count;
    private Integer m1308Stage2Count;
    private Integer m1308Stage3Count;
    private Integer m1308Stage4Count;
    private Integer m1308UnstageableCount;
    private String m1320PressureUlcerStatus;
    private String m1330StasisUlcer;
    private Integer m1332StasisUlcerCount;
    private String m1334StasisUlcerStatus;
    private String m1340SurgicalWound;
    private String m1342SurgicalWoundStatus;

    // ============================================
    // SECTION 6: Respiratory Status
    // ============================================
    
    private String m1400Dyspnea;
    private String m1410RespiratoryTreatments;

    // ============================================
    // SECTION 7: Cardiac Status
    // ============================================
    
    private String m1500HeartFailureSymptoms;

    // ============================================
    // SECTION 8: Elimination Status
    // ============================================
    
    private String m1600UtiTreatment;
    private String m1610UrinaryIncontinence;
    private String m1615IncontinenceTiming;
    private String m1620BowelIncontinence;
    private String m1630Ostomy;

    // ============================================
    // SECTION 9: Neurological/Emotional/Behavioral
    // ============================================
    
    private String m1700CognitiveFunctioning;
    private String m1710WhenConfused;
    private String m1720WhenAnxious;
    private String m1730DepressionScreening;
    private String m1740PsychiatricSymptoms;
    private String m1745DisruptiveBehaviorFreq;

    // ============================================
    // SECTION 10: Functional Abilities (GG Items)
    // ============================================
    
    private Map<String, Object> gg0100PriorFunctioning;
    private Map<String, Object> gg0110PriorDeviceUse;
    private Map<String, Object> gg0130SelfCare;
    private Map<String, Object> gg0170Mobility;

    // ============================================
    // SECTION 11: Medications
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
    // SECTION 12: Care Management
    // ============================================
    
    private Map<String, Object> m2102AssistanceTypes;
    private String m2110AssistanceFrequency;

    // ============================================
    // SECTION 13: Emergent Care
    // ============================================
    
    private String m2310EmergentCare;
    private String m2410EmergentCareReason;

    // ============================================
    // SECTION 14: COVID-19 Vaccination
    // ============================================
    
    private String o0350CovidVaccination;

    // ============================================
    // SECTION 15: Patient Mood (PHQ-2 to PHQ-9)
    // ============================================
    
    private String d0150Phq2Interest;
    private String d0150Phq2Depressed;
    private String d0150Phq9Q3;
    private String d0150Phq9Q4;
    private String d0150Phq9Q5;
    private String d0150Phq9Q6;
    private String d0150Phq9Q7;
    private String d0150Phq9Q8;
    private String d0150Phq9Q9;
    private Integer d0160Phq9TotalScore;

    // ============================================
    // SECTION 16: Immunization
    // ============================================
    
    private String m1041InfluenzaVaccinePeriod;
    private String m1046InfluenzaVaccineReceived;
    private String m1051PneumococcalVaccine;

    // ============================================
    // SECTION 17: Discharge Planning
    // ============================================
    
    private Map<String, Object> m2401InterventionSynopsis;
    private String m2410DischargeTo;
    private String m2420DischargeDisposition;
}

