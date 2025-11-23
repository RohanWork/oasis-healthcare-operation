package com.oasis.homehealth.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSelectionRequest {

    @NotNull(message = "Organization ID is required")
    private Long organizationId;
}

