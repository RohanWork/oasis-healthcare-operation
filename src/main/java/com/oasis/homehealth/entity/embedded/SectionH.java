package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section H — Bladder and Bowel
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionH {

    // M1600: Treated for UTI in past 14 days?
    @Column(name = "m1600_treated_uti", length = 1)
    private String m1600TreatedUti; // 0, 1, 2 (NA - on preventive regimen)

    // M1610: Urinary Incontinence or Catheter Presence
    @Column(name = "m1610_urinary_status", length = 1)
    private String m1610UrinaryStatus; // 0 = Continent, 1 = Incontinent, 2 = Catheter

    // M1620: Bowel Incontinence Frequency
    @Column(name = "m1620_bowel_incontinence", length = 1)
    private String m1620BowelIncontinence; // 0-5

    // M1630: Ostomy for Bowel Elimination
    @Column(name = "m1630_ostomy", length = 1)
    private String m1630Ostomy; // 0, 1
}

