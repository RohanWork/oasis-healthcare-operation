package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.OrganizationDTO;
import com.oasis.homehealth.dto.OrganizationRequest;
import com.oasis.homehealth.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Organization Management", description = "APIs for managing organizations (SYSTEM_ADMIN only)")
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Create organization", description = "Create a new organization (SYSTEM_ADMIN only)")
    public ResponseEntity<OrganizationDTO> createOrganization(@Valid @RequestBody OrganizationRequest request) {
        log.info("REST request to create organization: {}", request.getOrganizationCode());
        OrganizationDTO organization = organizationService.createOrganization(request);
        return new ResponseEntity<>(organization, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Update organization", description = "Update an existing organization (SYSTEM_ADMIN only)")
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {
        log.info("REST request to update organization: {}", id);
        OrganizationDTO organization = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(organization);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get all organizations", description = "Get all organizations (SYSTEM_ADMIN only)")
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        log.info("REST request to get all organizations");
        List<OrganizationDTO> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/my-organizations")
    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get my organizations", description = "Get organizations accessible to the current user")
    public ResponseEntity<List<OrganizationDTO>> getMyOrganizations(
            jakarta.servlet.http.HttpServletRequest request) {
        log.info("REST request to get my organizations");
        Long organizationId = (Long) request.getAttribute("organizationId");
        List<OrganizationDTO> organizations = organizationService.getMyOrganizations(organizationId);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get organization by ID", description = "Get organization details")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        log.info("REST request to get organization: {}", id);
        OrganizationDTO organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(organization);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete organization", description = "Soft delete an organization (SYSTEM_ADMIN only)")
    public ResponseEntity<Map<String, String>> deleteOrganization(@PathVariable Long id) {
        log.info("REST request to delete organization: {}", id);
        organizationService.deleteOrganization(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Organization deleted successfully");
        return ResponseEntity.ok(response);
    }
}

