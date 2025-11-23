package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section F — Living Situation & Caregiver Assistance
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionF {

    // M1100: Living Arrangement
    @Column(name = "m1100_living_arrangement", length = 1)
    private String m1100LivingArrangement; // A, B, C

    // M1100: Availability of Assistance
    @Column(name = "m1100_assistance_type", length = 2)
    private String m1100AssistanceType; // 01-15

    // M2102: Types & Sources of Assistance (SKIP if M1100 = C AND AssistanceType = 15)
    @Column(name = "m2102a_adl_assistance", length = 1)
    private String m2102aAdlAssistance; // 0-4

    @Column(name = "m2102c_medication_admin", length = 1)
    private String m2102cMedicationAdmin; // 0-4

    @Column(name = "m2102d_medical_procedures", length = 1)
    private String m2102dMedicalProcedures; // 0-4

    @Column(name = "m2102f_supervision_safety", length = 1)
    private String m2102fSupervisionSafety; // 0-4
}

