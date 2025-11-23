package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section N — Medications
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionN {

    // N0415: High-Risk Drug Classes: Use and Indication
    // Column 1: Is Taking
    @Column(name = "n0415_antipsychotic_taking")
    private Boolean n0415AntipsychoticTaking;

    @Column(name = "n0415_anticoagulant_taking")
    private Boolean n0415AnticoagulantTaking;

    @Column(name = "n0415_antibiotic_taking")
    private Boolean n0415AntibioticTaking;

    @Column(name = "n0415_opioid_taking")
    private Boolean n0415OpioidTaking;

    @Column(name = "n0415_antiplatelet_taking")
    private Boolean n0415AntiplateletTaking;

    @Column(name = "n0415_hypoglycemic_taking")
    private Boolean n0415HypoglycemicTaking;

    @Column(name = "n0415_none_of_above_taking")
    private Boolean n0415NoneOfAboveTaking;

    // Column 2: Indication Noted
    @Column(name = "n0415_antipsychotic_indication")
    private Boolean n0415AntipsychoticIndication;

    @Column(name = "n0415_anticoagulant_indication")
    private Boolean n0415AnticoagulantIndication;

    @Column(name = "n0415_antibiotic_indication")
    private Boolean n0415AntibioticIndication;

    @Column(name = "n0415_opioid_indication")
    private Boolean n0415OpioidIndication;

    @Column(name = "n0415_antiplatelet_indication")
    private Boolean n0415AntiplateletIndication;

    @Column(name = "n0415_hypoglycemic_indication")
    private Boolean n0415HypoglycemicIndication;

    @Column(name = "n0415_none_of_above_indication")
    private Boolean n0415NoneOfAboveIndication;

    // M2001: Drug Regimen Review
    @Column(name = "m2001_drug_regimen_review", length = 1)
    private String m2001DrugRegimenReview; // 0, 1, 9 (NA)

    // M2003: Medication Follow-up (SKIP if M2001 != 1)
    @Column(name = "m2003_medication_followup", length = 1)
    private String m2003MedicationFollowUp; // 0, 1

    // M2005: Medication Intervention (SKIP if M2001 != 1)
    @Column(name = "m2005_medication_intervention", length = 1)
    private String m2005MedicationIntervention; // 0, 1, 9 (NA)

    // M2010: Patient/Caregiver High-Risk Drug Education
    @Column(name = "m2010_high_risk_drug_education", length = 2)
    private String m2010HighRiskDrugEducation; // 0, 1, NA

    // M2020: Management of Oral Medications
    @Column(name = "m2020_oral_medications_management", length = 2)
    private String m2020OralMedicationsManagement; // 0-3, NA

    // M2030: Management of Injectable Medications
    @Column(name = "m2030_injectable_medications_management", length = 2)
    private String m2030InjectableMedicationsManagement; // 0-3, NA
}

