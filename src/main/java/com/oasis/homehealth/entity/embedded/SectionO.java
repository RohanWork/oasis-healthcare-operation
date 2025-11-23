package com.oasis.homehealth.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Section O — Special Treatments, Procedures, and Programs
 * This is a MASSIVE section with O0110 having 40+ checkboxes across 2 columns (admission + discharge)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionO {

    // O0110: Special Treatments, Procedures, and Programs
    // On Admission Column
    @Column(name = "o0110_a1_chemo_adm")
    private Boolean o0110A1ChemoAdm;

    @Column(name = "o0110_a2_iv_adm")
    private Boolean o0110A2IVAdm;

    @Column(name = "o0110_a3_oral_adm")
    private Boolean o0110A3OralAdm;

    @Column(name = "o0110_a10_other_adm")
    private Boolean o0110A10OtherAdm;

    @Column(name = "o0110_b1_radiation_adm")
    private Boolean o0110B1RadiationAdm;

    @Column(name = "o0110_c1_oxygen_therapy_adm")
    private Boolean o0110C1OxygenTherapyAdm;

    @Column(name = "o0110_c2_continuous_adm")
    private Boolean o0110C2ContinuousAdm;

    @Column(name = "o0110_c3_intermittent_adm")
    private Boolean o0110C3IntermittentAdm;

    @Column(name = "o0110_c4_high_conc_adm")
    private Boolean o0110C4HighConcAdm;

    @Column(name = "o0110_d1_suctioning_adm")
    private Boolean o0110D1SuctioningAdm;

    @Column(name = "o0110_d2_scheduled_adm")
    private Boolean o0110D2ScheduledAdm;

    @Column(name = "o0110_d3_as_needed_adm")
    private Boolean o0110D3AsNeededAdm;

    @Column(name = "o0110_e1_tracheostomy_care_adm")
    private Boolean o0110E1TracheostomyCareAdm;

    @Column(name = "o0110_f1_invasive_vent_adm")
    private Boolean o0110F1InvasiveVentAdm;

    @Column(name = "o0110_g1_non_invasive_vent_adm")
    private Boolean o0110G1NonInvasiveVentAdm;

    @Column(name = "o0110_g2_bipap_adm")
    private Boolean o0110G2BiPAPAdm;

    @Column(name = "o0110_g3_cpap_adm")
    private Boolean o0110G3CPAPAdm;

    @Column(name = "o0110_h1_iv_medications_adm")
    private Boolean o0110H1IVMedicationsAdm;

    @Column(name = "o0110_h2_vasoactive_meds_adm")
    private Boolean o0110H2VasoactiveMedsAdm;

    @Column(name = "o0110_h3_antibiotics_adm")
    private Boolean o0110H3AntibioticsAdm;

    @Column(name = "o0110_h4_anticoagulation_adm")
    private Boolean o0110H4AnticoagulationAdm;

    @Column(name = "o0110_h10_other_adm")
    private Boolean o0110H10OtherAdm;

    @Column(name = "o0110_i1_transfusions_adm")
    private Boolean o0110I1TransfusionsAdm;

    @Column(name = "o0110_j1_dialysis_adm")
    private Boolean o0110J1DialysisAdm;

    @Column(name = "o0110_j2_hemodialysis_adm")
    private Boolean o0110J2HemodialysisAdm;

    @Column(name = "o0110_j3_peritoneal_dialysis_adm")
    private Boolean o0110J3PeritonealDialysisAdm;

    @Column(name = "o0110_o1_iv_access_adm")
    private Boolean o0110O1IVAccessAdm;

    @Column(name = "o0110_o2_peripheral_adm")
    private Boolean o0110O2PeripheralAdm;

    @Column(name = "o0110_o3_midline_adm")
    private Boolean o0110O3MidlineAdm;

    @Column(name = "o0110_o4_central_adm")
    private Boolean o0110O4CentralAdm;

    @Column(name = "o0110_z1_none_adm")
    private Boolean o0110Z1NoneAdm;

    // At Discharge Column
    @Column(name = "o0110_a1_chemo_dis")
    private Boolean o0110A1ChemoDis;

    @Column(name = "o0110_a2_iv_dis")
    private Boolean o0110A2IVDis;

    @Column(name = "o0110_a3_oral_dis")
    private Boolean o0110A3OralDis;

    @Column(name = "o0110_a10_other_dis")
    private Boolean o0110A10OtherDis;

    @Column(name = "o0110_b1_radiation_dis")
    private Boolean o0110B1RadiationDis;

    @Column(name = "o0110_c1_oxygen_therapy_dis")
    private Boolean o0110C1OxygenTherapyDis;

    @Column(name = "o0110_c2_continuous_dis")
    private Boolean o0110C2ContinuousDis;

    @Column(name = "o0110_c3_intermittent_dis")
    private Boolean o0110C3IntermittentDis;

    @Column(name = "o0110_c4_high_conc_dis")
    private Boolean o0110C4HighConcDis;

    @Column(name = "o0110_d1_suctioning_dis")
    private Boolean o0110D1SuctioningDis;

    @Column(name = "o0110_d2_scheduled_dis")
    private Boolean o0110D2ScheduledDis;

    @Column(name = "o0110_d3_as_needed_dis")
    private Boolean o0110D3AsNeededDis;

    @Column(name = "o0110_e1_tracheostomy_care_dis")
    private Boolean o0110E1TracheostomyCareDis;

    @Column(name = "o0110_f1_invasive_vent_dis")
    private Boolean o0110F1InvasiveVentDis;

    @Column(name = "o0110_g1_non_invasive_vent_dis")
    private Boolean o0110G1NonInvasiveVentDis;

    @Column(name = "o0110_g2_bipap_dis")
    private Boolean o0110G2BiPAPDis;

    @Column(name = "o0110_g3_cpap_dis")
    private Boolean o0110G3CPAPDis;

    @Column(name = "o0110_h1_iv_medications_dis")
    private Boolean o0110H1IVMedicationsDis;

    @Column(name = "o0110_h2_vasoactive_meds_dis")
    private Boolean o0110H2VasoactiveMedsDis;

    @Column(name = "o0110_h3_antibiotics_dis")
    private Boolean o0110H3AntibioticsDis;

    @Column(name = "o0110_h4_anticoagulation_dis")
    private Boolean o0110H4AnticoagulationDis;

    @Column(name = "o0110_h10_other_dis")
    private Boolean o0110H10OtherDis;

    @Column(name = "o0110_i1_transfusions_dis")
    private Boolean o0110I1TransfusionsDis;

    @Column(name = "o0110_j1_dialysis_dis")
    private Boolean o0110J1DialysisDis;

    @Column(name = "o0110_j2_hemodialysis_dis")
    private Boolean o0110J2HemodialysisDis;

    @Column(name = "o0110_j3_peritoneal_dialysis_dis")
    private Boolean o0110J3PeritonealDialysisDis;

    @Column(name = "o0110_o1_iv_access_dis")
    private Boolean o0110O1IVAccessDis;

    @Column(name = "o0110_o2_peripheral_dis")
    private Boolean o0110O2PeripheralDis;

    @Column(name = "o0110_o3_midline_dis")
    private Boolean o0110O3MidlineDis;

    @Column(name = "o0110_o4_central_dis")
    private Boolean o0110O4CentralDis;

    @Column(name = "o0110_z1_none_dis")
    private Boolean o0110Z1NoneDis;

    // O0350: Patient's COVID-19 vaccination is up to date
    @Column(name = "o0350_covid_up_to_date", length = 1)
    private String o0350CovidUpToDate; // 0, 1

    // M1041: Influenza Vaccine Data Collection Period
    @Column(name = "m1041_influenza_collection_period", length = 1)
    private String m1041InfluenzaCollectionPeriod; // 0, 1

    // M1046: Influenza Vaccine Received (SKIP if M1041 = 0)
    @Column(name = "m1046_influenza_vaccine_received", length = 1)
    private String m1046InfluenzaVaccineReceived; // 1-8
}

