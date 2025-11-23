package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

/**
 * Section A — Administrative Information (Extensions to base entity)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionA {

    // A1005: Ethnicity
    @Column(name = "a1005_ethnicity", length = 50)
    private String a1005Ethnicity; // Hispanic or Latino, Not Hispanic or Latino, Unknown/Refused

    // A1010: Race (multiple selections, stored as comma-separated)
    @Column(name = "a1010_race", length = 200)
    private String a1010Race; // American Indian, Asian, Black, Native Hawaiian, White

    // A1110: Preferred Language
    @Column(name = "a1110_language", length = 50)
    private String a1110Language; // English, Spanish, Arabic, Hindi, Tagalog, Chinese, Other

    // These are typically in the main entity but included here for completeness
    @Column(name = "m0010_cms_cert_number", length = 12)
    private String m0010CmsCertNumber;

    @Column(name = "m0014_branch_state", length = 2)
    private String m0014BranchState;

    @Column(name = "m0016_branch_id", length = 10)
    private String m0016BranchId;

    @Column(name = "m0020_patient_id", length = 20)
    private String m0020PatientId;

    @Column(name = "m0030_start_of_care_date")
    private LocalDate m0030StartOfCareDate;

    @Column(name = "m0032_resumption_date")
    private LocalDate m0032ResumptionDate;

    @Column(name = "m0040_patient_name", length = 200)
    private String m0040PatientName;

    @Column(name = "m0045_medicare_number", length = 20)
    private String m0045MedicareNumber;

    @Column(name = "m0064_ssn", length = 11)
    private String m0064SSN;

    @Column(name = "m0065_gender", length = 1)
    private String m0065Gender; // M, F

    @Column(name = "m0069_birth_date")
    private LocalDate m0069BirthDate;
}

