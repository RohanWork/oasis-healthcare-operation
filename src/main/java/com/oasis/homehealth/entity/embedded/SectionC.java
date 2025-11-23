package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section C — Cognitive Patterns (BIMS + Delirium + Behavioral)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionC {

    // C0100: Should BIMS be conducted?
    @Column(name = "c0100_bims_conducted", length = 1)
    private String c0100BimsConducted; // 0 = No (Skip to C1310), 1 = Yes

    // BIMS (C0200-C0500) - SKIP if C0100 = 0
    @Column(name = "c0200_repetition_words")
    private Integer c0200RepetitionWords; // 0-3

    @Column(name = "c0300_temporal_orientation")
    private Integer c0300TemporalOrientation; // 0-3

    @Column(name = "c0400_recall")
    private Integer c0400Recall; // 0-3

    @Column(name = "c0500_bims_summary")
    private Integer c0500BimsSummary; // 0-15 (Auto-calculated)

    @Column(name = "c1000_decision_making", length = 1)
    private String c1000DecisionMaking; // 0-3

    // C1310: Delirium (Always visible)
    @Column(name = "c1310a_inattention", length = 1)
    private String c1310AInattention; // 0, 1

    @Column(name = "c1310b_disorganized_thinking", length = 1)
    private String c1310BDisorganizedThinking; // 0, 1

    @Column(name = "c1310c_altered_consciousness", length = 1)
    private String c1310CAlteredConsciousness; // 0, 1

    // M1700-M1720: Cognitive Function/Behavior
    @Column(name = "m1700_cognitive_function", length = 1)
    private String m1700CognitiveFunction; // 0-3

    @Column(name = "m1710_when_confused", length = 1)
    private String m1710WhenConfused; // 0-3

    @Column(name = "m1720_when_anxious", length = 1)
    private String m1720WhenAnxious; // 0-3
}

