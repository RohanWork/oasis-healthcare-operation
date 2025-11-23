package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section Q — Participation in Assessment and Goal Setting
 * M2401: Intervention Synopsis
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionQ {

    // M2401: Intervention Synopsis
    @Column(name = "m2401_falls_prevention", length = 2)
    private String m2401FallsPrevention; // 0, 1, NA

    @Column(name = "m2401_depression_interventions", length = 2)
    private String m2401DepressionInterventions; // 0, 1, NA

    @Column(name = "m2401_pain_interventions", length = 2)
    private String m2401PainInterventions; // 0, 1, NA

    @Column(name = "m2401_prevent_pressure_ulcers", length = 2)
    private String m2401PreventPressureUlcers; // 0, 1, NA

    @Column(name = "m2401_moist_wound_healing", length = 2)
    private String m2401MoistWoundHealing; // 0, 1, NA
}

