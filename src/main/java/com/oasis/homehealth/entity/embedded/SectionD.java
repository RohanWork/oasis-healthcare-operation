package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section D — Mood (PHQ-2 to Assess Depression)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionD {

    // D0150: PHQ-2 - Staff Assessment
    @Column(name = "d0150a1_little_interest_staff", length = 1)
    private String d0150A1; // 0-3, 9

    @Column(name = "d0150b1_feeling_down_staff", length = 1)
    private String d0150B1; // 0-3, 9

    // D0150: PHQ-2 - Frequency
    @Column(name = "d0150a2_little_interest_freq", length = 1)
    private String d0150A2; // 0-3

    @Column(name = "d0150b2_feeling_down_freq", length = 1)
    private String d0150B2; // 0-3

    // D0160: Total Severity Score (Auto-calculated)
    @Column(name = "d0160_total_score")
    private Integer d0160TotalScore; // 0-6 or null
}

