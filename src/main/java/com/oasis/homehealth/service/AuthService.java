package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.*;
import com.oasis.homehealth.dto.auth.*;
import com.oasis.homehealth.entity.Organization;
import com.oasis.homehealth.entity.Permission;
import com.oasis.homehealth.entity.Role;
import com.oasis.homehealth.entity.User;
import com.oasis.homehealth.repository.OrganizationRepository;
import com.oasis.homehealth.repository.UserRepository;
import com.oasis.homehealth.security.JwtTokenProvider;
import com.oasis.homehealth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login
        user.setLastLogin(LocalDate.now());
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        // Generate tokens without organization (will be added after selection)
        String accessToken = tokenProvider.generateToken(user.getId(), user.getUsername(), null);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId(), user.getUsername());

        // Convert roles to DTOs
        List<RoleDTO> roleDTOs = user.getRoles().stream()
                .map(this::convertRoleToDTO)
                .collect(Collectors.toList());

        // Convert organizations to DTOs
        List<OrganizationDTO> organizationDTOs = user.getOrganizations().stream()
                .map(this::convertOrganizationToDTO)
                .collect(Collectors.toList());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roleDTOs)
                .organizations(organizationDTOs)
                .requiresOrganizationSelection(!organizationDTOs.isEmpty())
                .build();
    }

    @Transactional
    public OrganizationSelectionResponse selectOrganization(Long userId, OrganizationSelectionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // SYSTEM_ADMIN can select any organization; others must be assigned to it
        boolean isSystemAdmin = user.getRoles().stream()
                .anyMatch(role -> "SYSTEM_ADMIN".equals(role.getRoleName()));
        
        if (!isSystemAdmin) {
            // Verify user has access to this organization
            boolean hasAccess = user.getOrganizations().stream()
                    .anyMatch(org -> org.getId().equals(organization.getId()));

            if (!hasAccess) {
                throw new RuntimeException("User does not have access to this organization");
            }
        }

        // Generate new token with organization ID
        String accessToken = tokenProvider.generateToken(user.getId(), user.getUsername(), organization.getId());

        // Get all permissions for this user's roles
        List<PermissionDTO> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .map(this::convertPermissionToDTO)
                .collect(Collectors.toList());

        return OrganizationSelectionResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .selectedOrganization(convertOrganizationToDTO(organization))
                .permissions(permissions)
                .message("Organization selected successfully")
                .build();
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    private RoleDTO convertRoleToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .roleLevel(role.getRoleLevel())
                .build();
    }

    private PermissionDTO convertPermissionToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .displayName(permission.getDisplayName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .action(permission.getAction())
                .build();
    }

    private OrganizationDTO convertOrganizationToDTO(Organization organization) {
        return OrganizationDTO.builder()
                .id(organization.getId())
                .organizationCode(organization.getOrganizationCode())
                .organizationName(organization.getOrganizationName())
                .legalName(organization.getLegalName())
                .taxId(organization.getTaxId())
                .npiNumber(organization.getNpiNumber())
                .phoneNumber(organization.getPhoneNumber())
                .faxNumber(organization.getFaxNumber())
                .email(organization.getEmail())
                .website(organization.getWebsite())
                .addressLine1(organization.getAddressLine1())
                .addressLine2(organization.getAddressLine2())
                .city(organization.getCity())
                .state(organization.getState())
                .zipCode(organization.getZipCode())
                .country(organization.getCountry())
                .organizationType(organization.getOrganizationType())
                .subscriptionTier(organization.getSubscriptionTier())
                .isTenantActive(organization.getIsTenantActive())
                .parentOrganizationId(organization.getParentOrganization() != null ? 
                        organization.getParentOrganization().getId() : null)
                .build();
    }
}

