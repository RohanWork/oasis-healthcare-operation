package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section J — Health Conditions (Pain, Falls, Risk for Hospitalization)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionJ {

    // M1033: Risk for Hospitalization
    @Column(name = "m1033_fall_history")
    private Boolean m1033FallHistory;

    @Column(name = "m1033_weight_loss")
    private Boolean m1033WeightLoss;

    @Column(name = "m1033_multi_hospitalizations")
    private Boolean m1033MultiHospitalizations;

    @Column(name = "m1033_multi_ed_visits")
    private Boolean m1033MultiEDVisits;

    @Column(name = "m1033_decline_status")
    private Boolean m1033DeclineStatus;

    @Column(name = "m1033_compliance_issues")
    private Boolean m1033ComplianceIssues;

    @Column(name = "m1033_polypharmacy")
    private Boolean m1033Polypharmacy;

    @Column(name = "m1033_exhaustion")
    private Boolean m1033Exhaustion;

    @Column(name = "m1033_other_risk")
    private Boolean m1033OtherRisk;

    @Column(name = "m1033_none")
    private Boolean m1033None;

    // J0510: Pain Effect on Sleep
    @Column(name = "j0510_pain_effect_sleep", length = 1)
    private String j0510PainEffectSleep; // 0-4, 8

    // J0520: Pain Interference with Therapy (SKIP if J0510 = 0)
    @Column(name = "j0520_pain_interf_therapy", length = 1)
    private String j0520PainInterfTherapy; // 0-4, 8

    // J0530: Pain Interference with Day-to-Day (SKIP if J0510 = 0)
    @Column(name = "j0530_pain_interf_day_to_day", length = 1)
    private String j0530PainInterfDayToDay; // 1-4, 8

    // J1800: Any Falls Since SOC/ROC
    @Column(name = "j1800_any_falls", length = 1)
    private String j1800AnyFalls; // 0, 1

    // J1900: Number of Falls Since SOC/ROC (by Injury Type) - SKIP if J1800 = 0
    @Column(name = "j1900a_no_injury", length = 1)
    private String j1900aNoInjury; // 0, 1, 2

    @Column(name = "j1900b_injury_except_major", length = 1)
    private String j1900bInjuryExceptMajor; // 0, 1, 2

    @Column(name = "j1900c_major_injury", length = 1)
    private String j1900cMajorInjury; // 0, 1, 2

    // M1400: When is the patient dyspneic or noticeably short of breath?
    @Column(name = "m1400_short_of_breath", length = 1)
    private String m1400ShortOfBreath; // 0-4
}

