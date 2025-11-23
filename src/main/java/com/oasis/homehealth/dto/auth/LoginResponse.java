package com.oasis.homehealth.dto.auth;

import com.oasis.homehealth.dto.OrganizationDTO;
import com.oasis.homehealth.dto.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private List<RoleDTO> roles;
    private List<OrganizationDTO> organizations;
    private boolean requiresOrganizationSelection;
}

