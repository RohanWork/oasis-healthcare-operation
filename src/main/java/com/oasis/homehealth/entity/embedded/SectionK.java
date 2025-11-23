package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section K — Swallowing / Nutritional Status
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionK {

    // M1060: Height and Weight
    @Column(name = "m1060_height_inches")
    private Integer m1060HeightInches;

    @Column(name = "m1060_weight_pounds")
    private Integer m1060WeightPounds;

    // K0520: Nutritional Approaches
    // On Admission
    @Column(name = "k0520a_parenteral_feeding_admission")
    private Boolean k0520AParenteralFeedingAdmission;

    @Column(name = "k0520b_feeding_tube_admission")
    private Boolean k0520BFeedingTubeAdmission;

    @Column(name = "k0520c_mech_altered_diet_admission")
    private Boolean k0520CMechAlteredDietAdmission;

    @Column(name = "k0520d_therapeutic_diet_admission")
    private Boolean k0520DTherapeuticDietAdmission;

    @Column(name = "k0520z_none_admission")
    private Boolean k0520ZNoneAdmission;

    // Last 7 Days
    @Column(name = "k0520a_parenteral_feeding_last7days")
    private Boolean k0520AParenteralFeedingLast7Days;

    @Column(name = "k0520b_feeding_tube_last7days")
    private Boolean k0520BFeedingTubeLast7Days;

    @Column(name = "k0520c_mech_altered_diet_last7days")
    private Boolean k0520CMechAlteredDietLast7Days;

    @Column(name = "k0520d_therapeutic_diet_last7days")
    private Boolean k0520DTherapeuticDietLast7Days;

    @Column(name = "k0520z_none_last7days")
    private Boolean k0520ZNoneLast7Days;

    // At Discharge
    @Column(name = "k0520a_parenteral_feeding_discharge")
    private Boolean k0520AParenteralFeedingDischarge;

    @Column(name = "k0520b_feeding_tube_discharge")
    private Boolean k0520BFeedingTubeDischarge;

    @Column(name = "k0520c_mech_altered_diet_discharge")
    private Boolean k0520CMechAlteredDietDischarge;

    @Column(name = "k0520d_therapeutic_diet_discharge")
    private Boolean k0520DTherapeuticDietDischarge;

    @Column(name = "k0520z_none_discharge")
    private Boolean k0520ZNoneDischarge;

    // M1870: Feeding or Eating
    @Column(name = "m1870_feeding_or_eating", length = 1)
    private String m1870FeedingOrEating; // 0-5
}

