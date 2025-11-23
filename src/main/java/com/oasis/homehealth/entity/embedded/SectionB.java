package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section B — Hearing, Speech, and Vision
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionB {

    @Column(name = "b0100_comatose", length = 1)
    private String b0100Comatose; // 0 = No, 1 = Yes

    @Column(name = "b0200_hearing", length = 1)
    private String b0200Hearing; // 0-4

    @Column(name = "b0300_speech", length = 1)
    private String b0300Speech; // 0-2

    @Column(name = "b1000_vision", length = 1)
    private String b1000Vision; // 0-4
}

