package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.OrganizationDTO;
import com.oasis.homehealth.dto.OrganizationRequest;
import com.oasis.homehealth.entity.Organization;
import com.oasis.homehealth.repository.OrganizationRepository;
import com.oasis.homehealth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    /**
     * Create a new organization (SYSTEM_ADMIN only)
     */
    public OrganizationDTO createOrganization(OrganizationRequest request) {
        log.info("Creating organization: {}", request.getOrganizationCode());

        // Check if organization code already exists
        if (organizationRepository.existsByOrganizationCode(request.getOrganizationCode())) {
            throw new RuntimeException("Organization code already exists: " + request.getOrganizationCode());
        }

        Organization organization = Organization.builder()
                .organizationCode(request.getOrganizationCode())
                .organizationName(request.getOrganizationName())
                .legalName(request.getLegalName())
                .taxId(request.getTaxId())
                .npiNumber(request.getNpiNumber())
                .phoneNumber(request.getPhoneNumber())
                .faxNumber(request.getFaxNumber())
                .email(request.getEmail())
                .website(request.getWebsite())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry() != null ? request.getCountry() : "USA")
                .licenseNumber(request.getLicenseNumber())
                .accreditation(request.getAccreditation())
                .organizationType(request.getOrganizationType())
                .subscriptionTier(request.getSubscriptionTier())
                .maxUsers(request.getMaxUsers())
                .maxPatients(request.getMaxPatients())
                .isTenantActive(request.getIsTenantActive() != null ? request.getIsTenantActive() : true)
                .build();

        // Set parent organization if provided
        if (request.getParentOrganizationId() != null) {
            Organization parent = organizationRepository.findById(request.getParentOrganizationId())
                    .orElseThrow(() -> new RuntimeException("Parent organization not found"));
            organization.setParentOrganization(parent);
        }

        organization.setIsActive(true);
        organization.setIsDeleted(false);

        Organization saved = organizationRepository.save(organization);
        log.info("Organization created: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Update an existing organization (SYSTEM_ADMIN only)
     */
    public OrganizationDTO updateOrganization(Long id, OrganizationRequest request) {
        log.info("Updating organization: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Check if organization code is being changed and if new code already exists
        if (!organization.getOrganizationCode().equals(request.getOrganizationCode()) &&
                organizationRepository.existsByOrganizationCode(request.getOrganizationCode())) {
            throw new RuntimeException("Organization code already exists: " + request.getOrganizationCode());
        }

        organization.setOrganizationCode(request.getOrganizationCode());
        organization.setOrganizationName(request.getOrganizationName());
        organization.setLegalName(request.getLegalName());
        organization.setTaxId(request.getTaxId());
        organization.setNpiNumber(request.getNpiNumber());
        organization.setPhoneNumber(request.getPhoneNumber());
        organization.setFaxNumber(request.getFaxNumber());
        organization.setEmail(request.getEmail());
        organization.setWebsite(request.getWebsite());
        organization.setAddressLine1(request.getAddressLine1());
        organization.setAddressLine2(request.getAddressLine2());
        organization.setCity(request.getCity());
        organization.setState(request.getState());
        organization.setZipCode(request.getZipCode());
        if (request.getCountry() != null) {
            organization.setCountry(request.getCountry());
        }
        organization.setLicenseNumber(request.getLicenseNumber());
        organization.setAccreditation(request.getAccreditation());
        organization.setOrganizationType(request.getOrganizationType());
        organization.setSubscriptionTier(request.getSubscriptionTier());
        organization.setMaxUsers(request.getMaxUsers());
        organization.setMaxPatients(request.getMaxPatients());
        if (request.getIsTenantActive() != null) {
            organization.setIsTenantActive(request.getIsTenantActive());
        }

        // Update parent organization if provided
        if (request.getParentOrganizationId() != null) {
            Organization parent = organizationRepository.findById(request.getParentOrganizationId())
                    .orElseThrow(() -> new RuntimeException("Parent organization not found"));
            organization.setParentOrganization(parent);
        } else if (request.getParentOrganizationId() == null && organization.getParentOrganization() != null) {
            // Remove parent if explicitly set to null
            organization.setParentOrganization(null);
        }

        Organization saved = organizationRepository.save(organization);
        log.info("Organization updated: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Get all organizations (SYSTEM_ADMIN only, or filtered by user's organizations)
     */
    @Transactional(readOnly = true)
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .filter(org -> !Boolean.TRUE.equals(org.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get organizations accessible to the current user
     * SYSTEM_ADMIN can see all organizations
     * ORG_ADMIN can see only their own organization
     */
    @Transactional(readOnly = true)
    public List<OrganizationDTO> getMyOrganizations(Long organizationId) {
        UserPrincipal currentUser = getCurrentUser();
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        if (isSystemAdmin) {
            // SYSTEM_ADMIN can see all organizations
            return getAllOrganizations();
        } else if (organizationId != null) {
            // ORG_ADMIN can see only their own organization
            Organization organization = organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            
            if (Boolean.TRUE.equals(organization.getIsDeleted())) {
                return Collections.emptyList();
            }
            
            return Collections.singletonList(convertToDTO(organization));
        } else {
            throw new RuntimeException("Organization ID is required for non-system administrators");
        }
    }

    /**
     * Get organization by ID
     */
    @Transactional(readOnly = true)
    public OrganizationDTO getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (Boolean.TRUE.equals(organization.getIsDeleted())) {
            throw new RuntimeException("Organization not found");
        }

        return convertToDTO(organization);
    }

    /**
     * Get current user from security context
     */
    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Soft delete organization (SYSTEM_ADMIN only)
     */
    public void deleteOrganization(Long id) {
        log.info("Deleting organization: {}", id);

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setIsDeleted(true);
        organization.setIsActive(false);
        organizationRepository.save(organization);

        log.info("Organization deleted: {}", id);
    }

    /**
     * Convert Organization entity to DTO
     */
    private OrganizationDTO convertToDTO(Organization organization) {
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

