package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section GG — Functional Abilities and Goals
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionGG {

    // GG0001: Not Applicable toggle
    @Column(name = "gg_na")
    private Boolean ggNA;

    // GG0100: Prior Functioning: Everyday Activities
    @Column(name = "gg0100_prior_self_care", length = 1)
    private String gg0100PriorSelfCare; // 1-3, 8

    @Column(name = "gg0100_prior_indoor_mobility", length = 1)
    private String gg0100PriorIndoorMobility; // 1-3, 8

    @Column(name = "gg0100_prior_stairs", length = 1)
    private String gg0100PriorStairs; // 1-3, 8

    @Column(name = "gg0100_prior_functional_cognition", length = 1)
    private String gg0100PriorFunctionalCognition; // 1-3, 8

    // GG0110: Prior Device Use
    @Column(name = "gg0110_prior_device_use", length = 10)
    private String gg0110PriorDeviceUse; // A, B, C, Z (multi-select stored as comma-separated)

    // GG0130: Self-Care (Admission, Goal)
    // A. Eating
    @Column(name = "gg0130a_eating_admission", length = 2)
    private String gg0130aEatingAdmission; // 01-07, 09

    @Column(name = "gg0130a_eating_goal", length = 2)
    private String gg0130aEatingGoal; // 03-06

    // B. Oral Hygiene
    @Column(name = "gg0130b_oral_hygiene_admission", length = 2)
    private String gg0130bOralHygieneAdmission; // 01-06

    @Column(name = "gg0130b_oral_hygiene_goal", length = 2)
    private String gg0130bOralHygieneGoal; // 04, 06

    // C. Toileting Hygiene
    @Column(name = "gg0130c_toileting_hygiene_admission", length = 2)
    private String gg0130cToiletingHygieneAdmission; // 01-06

    @Column(name = "gg0130c_toileting_hygiene_goal", length = 2)
    private String gg0130cToiletingHygieneGoal; // 04, 06

    // GG0170: Mobility (Admission, Goal)
    // B. Sit to Lying
    @Column(name = "gg0170b_sit_to_lying_admission", length = 2)
    private String gg0170bSitToLyingAdmission; // 01-06

    @Column(name = "gg0170b_sit_to_lying_goal", length = 2)
    private String gg0170bSitToLyingGoal; // 04, 06

    // C. Lying to Sitting on side of bed
    @Column(name = "gg0170c_lying_to_sitting_admission", length = 2)
    private String gg0170cLyingToSittingAdmission; // 01-06

    @Column(name = "gg0170c_lying_to_sitting_goal", length = 2)
    private String gg0170cLyingToSittingGoal; // 04, 06

    // D. Sit to Stand
    @Column(name = "gg0170d_sit_to_stand_admission", length = 2)
    private String gg0170dSitToStandAdmission; // 01-06

    @Column(name = "gg0170d_sit_to_stand_goal", length = 2)
    private String gg0170dSitToStandGoal; // 04, 06

    // E. Chair/Bed-to-Chair transfer
    @Column(name = "gg0170e_chair_bed_transfer_admission", length = 2)
    private String gg0170eChairBedTransferAdmission; // 01-06

    @Column(name = "gg0170e_chair_bed_transfer_goal", length = 2)
    private String gg0170eChairBedTransferGoal; // 04, 06

    // F. Toilet transfer
    @Column(name = "gg0170f_toilet_transfer_admission", length = 2)
    private String gg0170fToiletTransferAdmission; // 01-06

    @Column(name = "gg0170f_toilet_transfer_goal", length = 2)
    private String gg0170fToiletTransferGoal; // 04, 06

    // J. Walk 50 feet with two turns
    @Column(name = "gg0170j_walk_50_feet_2_turns_admission", length = 2)
    private String gg0170jWalk50Feet2TurnsAdmission; // 01-06

    @Column(name = "gg0170j_walk_50_feet_2_turns_goal", length = 2)
    private String gg0170jWalk50Feet2TurnsGoal; // 04, 06

    // K. Walk 150 feet
    @Column(name = "gg0170k_walk_150_feet_admission", length = 2)
    private String gg0170kWalk150FeetAdmission; // 01-06

    @Column(name = "gg0170k_walk_150_feet_goal", length = 2)
    private String gg0170kWalk150FeetGoal; // 04, 06

    // (Additional GG0170 items can be added as needed)
}

