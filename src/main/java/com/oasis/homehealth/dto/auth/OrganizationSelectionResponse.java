package com.oasis.homehealth.dto.auth;

import com.oasis.homehealth.dto.OrganizationDTO;
import com.oasis.homehealth.dto.PermissionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationSelectionResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private OrganizationDTO selectedOrganization;
    private List<PermissionDTO> permissions;
    private String message;
}

