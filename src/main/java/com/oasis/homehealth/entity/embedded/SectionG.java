package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section G — Functional Status: Mobility and Transfers
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionG {

    // G0100: Patient is bedfast / non-ambulatory
    @Column(name = "g0100_bedfast")
    private Boolean g0100Bedfast;

    // M1800: Grooming (disabled if bedfast)
    @Column(name = "m1800_grooming", length = 2)
    private String m1800Grooming; // 0-4, 99

    // M1810: Dressing Upper Body
    @Column(name = "m1810_current_dressing_upper", length = 2)
    private String m1810CurrentDressingUpper; // 0-4, 99

    // M1820: Dressing Lower Body
    @Column(name = "m1820_current_dressing_lower", length = 2)
    private String m1820CurrentDressingLower; // 0-4, 99

    // M1830: Bathing
    @Column(name = "m1830_bathing", length = 2)
    private String m1830Bathing; // 0-4, 99

    // M1840: Toilet Transferring
    @Column(name = "m1840_toilet_transferring", length = 2)
    private String m1840ToiletTransferring; // 0-4, 99

    // M1850: Transferring
    @Column(name = "m1850_transferring", length = 2)
    private String m1850Transferring; // 0-4, 99

    // M1860: Ambulation / Locomotion
    @Column(name = "m1860_ambulation_stairs", length = 2)
    private String m1860AmbulationStairs; // 0-4, 99
}

