package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section E — Behavioral Symptoms
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionE {

    // E0100: Behavioral Symptoms (observed in past 7 days)
    @Column(name = "e0100a_physical", length = 1)
    private String e0100APhysical; // 0-3

    @Column(name = "e0100b_verbal", length = 1)
    private String e0100BVerbal; // 0-3

    @Column(name = "e0100c_disruptive", length = 1)
    private String e0100CDisruptive; // 0-3

    @Column(name = "e0100d_socially_inappropriate", length = 1)
    private String e0100DSociallyInappropriate; // 0-3

    @Column(name = "e0100e_wandering", length = 1)
    private String e0100EWandering; // 0-3

    // E0300: Psychotic Symptoms (SKIP if all E0100 = 0)
    @Column(name = "e0300_psychotic_symptoms", length = 1)
    private String e0300PsychoticSymptoms; // 0, 1
}

