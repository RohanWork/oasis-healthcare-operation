package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section I — Active Diagnoses
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionI {

    // M1021: Primary Diagnosis
    @Column(name = "m1021_primary_diagnosis_description", length = 500)
    private String m1021PrimaryDiagnosisDescription;

    @Column(name = "m1021_primary_diagnosis_code", length = 7)
    private String m1021PrimaryDiagnosisCode;

    @Column(name = "m1021_primary_symptom_control", length = 1)
    private String m1021PrimarySymptomControl; // 0-4

    // M1023: Other Diagnoses (b-f)
    @Column(name = "m1023b_other_diagnosis_description", length = 500)
    private String m1023bOtherDiagnosisDescription;

    @Column(name = "m1023b_other_diagnosis_code", length = 7)
    private String m1023bOtherDiagnosisCode;

    @Column(name = "m1023b_symptom_control", length = 1)
    private String m1023bSymptomControl;

    @Column(name = "m1023c_other_diagnosis_description", length = 500)
    private String m1023cOtherDiagnosisDescription;

    @Column(name = "m1023c_other_diagnosis_code", length = 7)
    private String m1023cOtherDiagnosisCode;

    @Column(name = "m1023c_symptom_control", length = 1)
    private String m1023cSymptomControl;

    @Column(name = "m1023d_other_diagnosis_description", length = 500)
    private String m1023dOtherDiagnosisDescription;

    @Column(name = "m1023d_other_diagnosis_code", length = 7)
    private String m1023dOtherDiagnosisCode;

    @Column(name = "m1023d_symptom_control", length = 1)
    private String m1023dSymptomControl;

    @Column(name = "m1023e_other_diagnosis_description", length = 500)
    private String m1023eOtherDiagnosisDescription;

    @Column(name = "m1023e_other_diagnosis_code", length = 7)
    private String m1023eOtherDiagnosisCode;

    @Column(name = "m1023e_symptom_control", length = 1)
    private String m1023eSymptomControl;

    @Column(name = "m1023f_other_diagnosis_description", length = 500)
    private String m1023fOtherDiagnosisDescription;

    @Column(name = "m1023f_other_diagnosis_code", length = 7)
    private String m1023fOtherDiagnosisCode;

    @Column(name = "m1023f_symptom_control", length = 1)
    private String m1023fSymptomControl;

    // M1028: Active Diagnoses — Comorbidities
    @Column(name = "m1028_pvd_active")
    private Boolean m1028PvdActive;

    @Column(name = "m1028_diabetes_active")
    private Boolean m1028DiabetesActive;

    @Column(name = "m1028_none")
    private Boolean m1028None;
}

