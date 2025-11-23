package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.OasisAssessmentDTO;
import com.oasis.homehealth.dto.OasisAssessmentRequest;
import com.oasis.homehealth.dto.OasisQARequest;
import com.oasis.homehealth.service.OasisAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.oasis.homehealth.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/oasis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "OASIS Assessment", description = "OASIS-E1 Assessment Management APIs")
public class OasisAssessmentController {

    private final OasisAssessmentService oasisService;

    @PostMapping
    @PreAuthorize("hasAuthority('OASIS_CREATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_RN', 'ROLE_PT', 'ROLE_QA_NURSE')")
    @Operation(summary = "Create new OASIS assessment", description = "Create a new OASIS-E1 assessment for a patient")
    public ResponseEntity<OasisAssessmentDTO> createAssessment(
            @Valid @RequestBody OasisAssessmentRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to create OASIS assessment for patient: {}", request.getPatientId());
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        OasisAssessmentDTO result = oasisService.createAssessment(request, organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OASIS_UPDATE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Update OASIS assessment", description = "Update an existing OASIS assessment (manual save)")
    public ResponseEntity<OasisAssessmentDTO> updateAssessment(
            @PathVariable Long id,
            @Valid @RequestBody OasisAssessmentRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to update OASIS assessment: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        OasisAssessmentDTO result = oasisService.updateAssessment(id, request, organizationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/auto-save")
    @PreAuthorize("hasAuthority('OASIS_UPDATE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Auto-save OASIS assessment", description = "Auto-save assessment (called every 15 seconds)")
    public ResponseEntity<OasisAssessmentDTO> autoSaveAssessment(
            @PathVariable Long id,
            @RequestBody OasisAssessmentRequest request,
            HttpServletRequest httpRequest) {
        log.debug("REST request to auto-save OASIS assessment: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        OasisAssessmentDTO result = oasisService.autoSaveAssessment(id, request, organizationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('OASIS_SUBMIT') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Submit for QA review", description = "Submit OASIS assessment for QA review")
    public ResponseEntity<OasisAssessmentDTO> submitForQA(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to submit OASIS assessment for QA: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        OasisAssessmentDTO result = oasisService.submitForQA(id, organizationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/qa/review")
    @PreAuthorize("hasAuthority('OASIS_APPROVE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "QA Review", description = "Approve or reject OASIS assessment")
    public ResponseEntity<OasisAssessmentDTO> reviewAssessment(@Valid @RequestBody OasisQARequest request) {
        log.info("REST request to QA review OASIS assessment: {}", request.getAssessmentId());
        OasisAssessmentDTO result = oasisService.reviewAssessment(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/lock")
    @PreAuthorize("hasAuthority('OASIS_APPROVE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Lock assessment", description = "Lock OASIS assessment (final lock)")
    public ResponseEntity<OasisAssessmentDTO> lockAssessment(@PathVariable Long id) {
        log.info("REST request to lock OASIS assessment: {}", id);
        OasisAssessmentDTO result = oasisService.lockAssessment(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OASIS_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get assessment by ID", description = "Get OASIS assessment details")
    public ResponseEntity<OasisAssessmentDTO> getAssessment(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to get OASIS assessment: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        OasisAssessmentDTO result = oasisService.getAssessment(id, organizationId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('OASIS_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get assessments by patient", description = "Get all OASIS assessments for a patient")
    public ResponseEntity<List<OasisAssessmentDTO>> getAssessmentsByPatient(@PathVariable Long patientId) {
        log.info("REST request to get OASIS assessments for patient: {}", patientId);
        List<OasisAssessmentDTO> result = oasisService.getAssessmentsByPatient(patientId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/episode/{episodeId}")
    @PreAuthorize("hasAuthority('OASIS_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get assessments by episode", description = "Get all OASIS assessments for an episode")
    public ResponseEntity<List<OasisAssessmentDTO>> getAssessmentsByEpisode(@PathVariable Long episodeId) {
        log.info("REST request to get OASIS assessments for episode: {}", episodeId);
        List<OasisAssessmentDTO> result = oasisService.getAssessmentsByEpisode(episodeId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/qa/pending")
    @PreAuthorize("hasAuthority('OASIS_APPROVE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_QA_NURSE', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Get pending QA reviews", description = "Get all assessments pending QA review")
    public ResponseEntity<List<OasisAssessmentDTO>> getPendingQAReviews(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("REST request to get pending QA reviews");
        try {
            Long organizationId = (Long) httpRequest.getAttribute("organizationId");
            
            // SYSTEM_ADMIN can access all pending reviews without organization context
            // Check both @AuthenticationPrincipal and SecurityContextHolder as fallback
            boolean isSystemAdmin = false;
            if (currentUser != null) {
                isSystemAdmin = currentUser.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
            } else {
                // Fallback to SecurityContextHolder
                var authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                    isSystemAdmin = userPrincipal.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
                }
            }
            
            log.debug("User: {}, isSystemAdmin: {}, organizationId: {}", 
                currentUser != null ? currentUser.getUsername() : "unknown", isSystemAdmin, organizationId);
            
            if (organizationId == null && !isSystemAdmin) {
                log.error("Organization ID is null and user is not SYSTEM_ADMIN. User: {}", 
                    currentUser != null ? currentUser.getUsername() : "null");
                throw new RuntimeException("Organization ID is required. Please select an organization.");
            }
            
            List<OasisAssessmentDTO> result = oasisService.getPendingQAReviews(organizationId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Error getting pending QA reviews", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting pending QA reviews", e);
            throw new RuntimeException("Failed to retrieve pending QA reviews: " + e.getMessage(), e);
        }
    }

    @GetMapping("/incomplete")
    @PreAuthorize("hasAuthority('OASIS_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get incomplete assessments", description = "Get all incomplete (DRAFT) assessments")
    public ResponseEntity<List<OasisAssessmentDTO>> getIncompleteAssessments(HttpServletRequest httpRequest) {
        log.info("REST request to get incomplete assessments");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        List<OasisAssessmentDTO> result = oasisService.getIncompleteAssessments(organizationId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OASIS_DELETE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete assessment", description = "Soft delete OASIS assessment")
    public ResponseEntity<Void> deleteAssessment(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to delete OASIS assessment: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        oasisService.deleteAssessment(id, organizationId);
        return ResponseEntity.noContent().build();
    }
}

