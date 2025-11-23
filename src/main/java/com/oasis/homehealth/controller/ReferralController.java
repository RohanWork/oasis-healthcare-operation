package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.ReferralDTO;
import com.oasis.homehealth.dto.ReferralRequest;
import com.oasis.homehealth.service.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/referrals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Referral Management", description = "Referral Management APIs")
public class ReferralController {

    private final ReferralService referralService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_INTAKE_COORDINATOR', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get all referrals", description = "Get all referrals for the current organization")
    public ResponseEntity<List<ReferralDTO>> getAllReferrals(HttpServletRequest httpRequest) {
        log.info("REST request to get all referrals");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        List<ReferralDTO> referrals = referralService.getAllReferrals(organizationId);
        return ResponseEntity.ok(referrals);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_INTAKE_COORDINATOR', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get referral by ID", description = "Get a specific referral by ID")
    public ResponseEntity<ReferralDTO> getReferralById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to get referral: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        ReferralDTO referral = referralService.getReferralById(id, organizationId);
        return ResponseEntity.ok(referral);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_INTAKE_COORDINATOR', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Create new referral", description = "Create a new referral")
    public ResponseEntity<ReferralDTO> createReferral(
            @Valid @RequestBody ReferralRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to create referral");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        ReferralDTO referral = referralService.createReferral(request, organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(referral);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_INTAKE_COORDINATOR', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Update referral", description = "Update an existing referral")
    public ResponseEntity<ReferralDTO> updateReferral(
            @PathVariable Long id,
            @Valid @RequestBody ReferralRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to update referral: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        ReferralDTO referral = referralService.updateReferral(id, request, organizationId);
        return ResponseEntity.ok(referral);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_INTAKE_COORDINATOR', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete referral", description = "Delete a referral")
    public ResponseEntity<Void> deleteReferral(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to delete referral: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        referralService.deleteReferral(id, organizationId);
        return ResponseEntity.noContent().build();
    }
}

