package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OasisQARequest {

    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;

    @NotBlank(message = "Action is required")
    private String action; // APPROVE, REJECT

    private String comments; // Required for REJECT, optional for APPROVE
}

