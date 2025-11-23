package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.BillingClaimDTO;
import com.oasis.homehealth.dto.BillingClaimRequest;
import com.oasis.homehealth.service.BillingService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Billing Management", description = "APIs for managing billing claims")
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/claims")
    @PreAuthorize("hasAnyAuthority('BILLING_CREATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST')")
    @Operation(summary = "Create billing claim", description = "Create a new billing claim")
    public ResponseEntity<BillingClaimDTO> createClaim(
            @Valid @RequestBody BillingClaimRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to create billing claim");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        BillingClaimDTO claim = billingService.createClaim(request, organizationId);
        return new ResponseEntity<>(claim, HttpStatus.CREATED);
    }

    @PutMapping("/claims/{id}")
    @PreAuthorize("hasAnyAuthority('BILLING_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST')")
    @Operation(summary = "Update billing claim", description = "Update an existing billing claim")
    public ResponseEntity<BillingClaimDTO> updateClaim(
            @PathVariable Long id,
            @Valid @RequestBody BillingClaimRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to update billing claim: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        BillingClaimDTO claim = billingService.updateClaim(id, request, organizationId);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/claims/{id}")
    @PreAuthorize("hasAnyAuthority('BILLING_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get billing claim by ID", description = "Retrieve a billing claim by ID")
    public ResponseEntity<BillingClaimDTO> getClaimById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to get billing claim: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        BillingClaimDTO claim = billingService.getClaimById(id, organizationId);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/claims")
    @PreAuthorize("hasAnyAuthority('BILLING_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get all billing claims", description = "Retrieve all billing claims for the organization")
    public ResponseEntity<List<BillingClaimDTO>> getAllClaims(HttpServletRequest httpRequest) {
        log.info("REST request to get all billing claims");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<BillingClaimDTO> claims = billingService.getAllClaims(organizationId);
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/claims/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('BILLING_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get billing claims by patient", description = "Retrieve all billing claims for a patient")
    public ResponseEntity<List<BillingClaimDTO>> getClaimsByPatient(
            @PathVariable Long patientId,
            HttpServletRequest httpRequest) {
        log.info("REST request to get billing claims for patient: {}", patientId);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<BillingClaimDTO> claims = billingService.getClaimsByPatient(patientId, organizationId);
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/claims/episode/{episodeId}")
    @PreAuthorize("hasAnyAuthority('BILLING_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get billing claims by episode", description = "Retrieve all billing claims for an episode")
    public ResponseEntity<List<BillingClaimDTO>> getClaimsByEpisode(
            @PathVariable Long episodeId,
            HttpServletRequest httpRequest) {
        log.info("REST request to get billing claims for episode: {}", episodeId);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<BillingClaimDTO> claims = billingService.getClaimsByEpisode(episodeId, organizationId);
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/claims/status/{status}")
    @PreAuthorize("hasAnyAuthority('BILLING_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get billing claims by status", description = "Retrieve all billing claims with a specific status")
    public ResponseEntity<List<BillingClaimDTO>> getClaimsByStatus(
            @PathVariable String status,
            HttpServletRequest httpRequest) {
        log.info("REST request to get billing claims with status: {}", status);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<BillingClaimDTO> claims = billingService.getClaimsByStatus(status, organizationId);
        return ResponseEntity.ok(claims);
    }

    @PostMapping("/claims/{id}/submit")
    @PreAuthorize("hasAnyAuthority('BILLING_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST')")
    @Operation(summary = "Submit billing claim", description = "Submit a billing claim for processing")
    public ResponseEntity<BillingClaimDTO> submitClaim(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to submit billing claim: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        BillingClaimDTO claim = billingService.submitClaim(id, organizationId);
        return ResponseEntity.ok(claim);
    }

    @PostMapping("/claims/{id}/mark-paid")
    @PreAuthorize("hasAnyAuthority('BILLING_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST')")
    @Operation(summary = "Mark claim as paid", description = "Mark a billing claim as paid")
    public ResponseEntity<BillingClaimDTO> markAsPaid(
            @PathVariable Long id,
            @RequestParam BigDecimal paidAmount,
            HttpServletRequest httpRequest) {
        log.info("REST request to mark billing claim as paid: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        BillingClaimDTO claim = billingService.markAsPaid(id, paidAmount, organizationId);
        return ResponseEntity.ok(claim);
    }

    @PostMapping("/claims/{id}/mark-denied")
    @PreAuthorize("hasAnyAuthority('BILLING_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST')")
    @Operation(summary = "Mark claim as denied", description = "Mark a billing claim as denied")
    public ResponseEntity<BillingClaimDTO> markAsDenied(
            @PathVariable Long id,
            @RequestParam String denialReason,
            HttpServletRequest httpRequest) {
        log.info("REST request to mark billing claim as denied: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        BillingClaimDTO claim = billingService.markAsDenied(id, denialReason, organizationId);
        return ResponseEntity.ok(claim);
    }

    @DeleteMapping("/claims/{id}")
    @PreAuthorize("hasAnyAuthority('BILLING_DELETE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_BILLING_SPECIALIST')")
    @Operation(summary = "Delete billing claim", description = "Soft delete a billing claim")
    public ResponseEntity<Map<String, String>> deleteClaim(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to delete billing claim: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        billingService.deleteClaim(id, organizationId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Billing claim deleted successfully");
        return ResponseEntity.ok(response);
    }
}

