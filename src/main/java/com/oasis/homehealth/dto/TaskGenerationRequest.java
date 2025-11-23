package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGenerationRequest {
    @NotNull
    private Long planOfCareId;
    
    // Optional: specific start date (defaults to POC start date)
    private java.time.LocalDate startDate;
    
    // Optional: assign to specific clinician (defaults to admitting clinician)
    private Long assignToClinicianId;
    
    // Optional: generate only specific disciplines
    private java.util.List<String> disciplinesToGenerate; // e.g., ["RN", "PT"]
}
