package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section M — Skin Conditions (Pressure Ulcers, Stasis Ulcers, Surgical Wounds)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionM {

    // M1306: Does patient have at least one unhealed pressure ulcer/injury at Stage 2+?
    @Column(name = "m1306_unhealed_pressure_ulcer", length = 1)
    private String m1306UnhealedPressureUlcer; // 0, 1

    // M1307: Oldest Stage 2 pressure ulcer (SKIP if M1306 = 0)
    @Column(name = "m1307_oldest_stage2_code", length = 2)
    private String m1307OldestStage2Code; // 1, 2, NA

    @Column(name = "m1307_date_identified_month")
    private Integer m1307DateIdentifiedMonth;

    @Column(name = "m1307_date_identified_day")
    private Integer m1307DateIdentifiedDay;

    @Column(name = "m1307_date_identified_year")
    private Integer m1307DateIdentifiedYear;

    // M1311: Current Number of Unhealed Pressure Ulcers at Each Stage (SKIP if M1306 = 0)
    // SOC/ROC
    @Column(name = "m1311a_stage2")
    private Integer m1311AStage2;

    @Column(name = "m1311b1_stage3")
    private Integer m1311B1Stage3;

    @Column(name = "m1311c1_stage4")
    private Integer m1311C1Stage4;

    @Column(name = "m1311d1_unstageable_non_removable")
    private Integer m1311D1UnstageableNonRemovable;

    @Column(name = "m1311e1_unstageable_slough_eschar")
    private Integer m1311E1UnstageableSloughEschar;

    @Column(name = "m1311f1_unstageable_deep_tissue")
    private Integer m1311F1UnstageableDeepTissue;

    // Discharge
    @Column(name = "m1311a2_stage2_at_discharge")
    private Integer m1311A2Stage2AtDischarge;

    @Column(name = "m1311b2_stage3_at_discharge")
    private Integer m1311B2Stage3AtDischarge;

    @Column(name = "m1311c2_stage4_at_discharge")
    private Integer m1311C2Stage4AtDischarge;

    @Column(name = "m1311d2_unstageable_non_removable_at_discharge")
    private Integer m1311D2UnstageableNonRemovableAtDischarge;

    @Column(name = "m1311e2_unstageable_slough_eschar_at_discharge")
    private Integer m1311E2UnstageableSloughEscharAtDischarge;

    @Column(name = "m1311f2_unstageable_deep_tissue_at_discharge")
    private Integer m1311F2UnstageableDeepTissueAtDischarge;

    // M1322: Current Number of Stage 1 Pressure Injuries
    @Column(name = "m1322_number_stage1")
    private Integer m1322NumberStage1;

    // M1324: Stage of Most Problematic Unhealed Pressure Ulcer/Injury that is Stageable
    @Column(name = "m1324_stage_most_problematic", length = 1)
    private String m1324StageMostProblematic; // 1-4, 5 (Not applicable)

    // M1330: Does this patient have a Stasis Ulcer?
    @Column(name = "m1330_stasis_ulcer_present", length = 1)
    private String m1330StasisUlcerPresent; // 0-3

    // M1332: Current Number of Stasis Ulcers that are Observable (SKIP if M1330 = 0 or 3)
    @Column(name = "m1332_number_not_healing")
    private Integer m1332NumberNotHealing;

    @Column(name = "m1332_number_fully_granulating")
    private Integer m1332NumberFullyGranulating;

    @Column(name = "m1332_number_early_partial_granulation")
    private Integer m1332NumberEarlyPartialGranulation;

    @Column(name = "m1332_number_fully_epithelialized")
    private Integer m1332NumberFullyEpithelialized;

    // M1334: Status of Most Problematic Stasis Ulcer that is Observable (SKIP if M1330 = 0 or 3)
    @Column(name = "m1334_stasis_ulcer_status", length = 1)
    private String m1334StasisUlcerStatus; // 1-3

    // M1340: Does this patient have a Surgical Wound?
    @Column(name = "m1340_surgical_wound_present", length = 1)
    private String m1340SurgicalWoundPresent; // 0-2

    // M1342: Status of Most Problematic Surgical Wound that is Observable (SKIP if M1340 = 0 or 2)
    @Column(name = "m1342_surgical_wound_status", length = 1)
    private String m1342SurgicalWoundStatus; // 0-3
}

