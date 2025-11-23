package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.PlanOfCareDTO;
import com.oasis.homehealth.dto.PlanOfCareRequest;
import com.oasis.homehealth.security.UserPrincipal;
import com.oasis.homehealth.service.PlanOfCareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plan-of-care")
@Tag(name = "Plan of Care", description = "APIs for managing Plans of Care")
@RequiredArgsConstructor
public class PlanOfCareController {

    private final PlanOfCareService pocService;

    @PostMapping("/generate/{oasisId}")
    @PreAuthorize("hasAnyAuthority('POC_GENERATE', 'POC_CREATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_QA_NURSE', 'ROLE_RN', 'ROLE_PT')")
    @Operation(summary = "Generate POC from OASIS", description = "Auto-generate Plan of Care from approved OASIS assessment")
    public ResponseEntity<PlanOfCareDTO> generateFromOASIS(@PathVariable Long oasisId) {
        PlanOfCareDTO poc = pocService.generateFromOASIS(oasisId);
        return new ResponseEntity<>(poc, HttpStatus.CREATED);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('POC_CREATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_QA_NURSE', 'ROLE_RN', 'ROLE_PT')")
    @Operation(summary = "Create Plan of Care", description = "Create a new Plan of Care manually")
    public ResponseEntity<PlanOfCareDTO> createPOC(
            @Valid @RequestBody PlanOfCareRequest request,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        PlanOfCareDTO poc = pocService.createPOC(request, organizationId);
        return new ResponseEntity<>(poc, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('POC_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_QA_NURSE', 'ROLE_RN', 'ROLE_PT')")
    @Operation(summary = "Update Plan of Care", description = "Update an existing Plan of Care")
    public ResponseEntity<PlanOfCareDTO> updatePOC(
            @PathVariable Long id,
            @Valid @RequestBody PlanOfCareRequest request) {
        PlanOfCareDTO poc = pocService.updatePOC(id, request);
        return ResponseEntity.ok(poc);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POC_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get POC by ID", description = "Retrieve Plan of Care details by ID")
    public ResponseEntity<PlanOfCareDTO> getPOCById(@PathVariable Long id) {
        PlanOfCareDTO poc = pocService.getPOCById(id);
        return ResponseEntity.ok(poc);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('POC_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get all POCs", description = "Retrieve all Plans of Care based on user role")
    public ResponseEntity<List<PlanOfCareDTO>> getAllPOCs(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // Role-based filtering: Clinicians see only their assigned patients' POCs
        if (hasClinicianRole(currentUser)) {
            List<PlanOfCareDTO> pocs = pocService.getPOCsForClinician(organizationId, currentUser.getId());
            return ResponseEntity.ok(pocs);
        }
        
        // Admin/support see all POCs
        List<PlanOfCareDTO> pocs = pocService.getAllPOCs(organizationId);
        return ResponseEntity.ok(pocs);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('POC_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get POCs by patient", description = "Retrieve all Plans of Care for a specific patient")
    public ResponseEntity<List<PlanOfCareDTO>> getPOCsByPatient(@PathVariable Long patientId) {
        List<PlanOfCareDTO> pocs = pocService.getPOCsByPatient(patientId);
        return ResponseEntity.ok(pocs);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('POC_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get POCs by status", description = "Retrieve Plans of Care by status")
    public ResponseEntity<List<PlanOfCareDTO>> getPOCsByStatus(
            @PathVariable String status,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<PlanOfCareDTO> pocs = pocService.getPOCsByStatus(organizationId, status);
        return ResponseEntity.ok(pocs);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('POC_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Submit POC for approval", description = "Submit Plan of Care for approval")
    public ResponseEntity<PlanOfCareDTO> submitForApproval(@PathVariable Long id) {
        PlanOfCareDTO poc = pocService.submitForApproval(id);
        return ResponseEntity.ok(poc);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('POC_APPROVE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Approve POC", description = "Approve a Plan of Care")
    public ResponseEntity<PlanOfCareDTO> approvePOC(@PathVariable Long id) {
        PlanOfCareDTO poc = pocService.approvePOC(id);
        return ResponseEntity.ok(poc);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('POC_APPROVE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Reject POC", description = "Reject a Plan of Care with reason")
    public ResponseEntity<PlanOfCareDTO> rejectPOC(
            @PathVariable Long id,
            @RequestParam String reason) {
        PlanOfCareDTO poc = pocService.rejectPOC(id, reason);
        return ResponseEntity.ok(poc);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('POC_DELETE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Delete POC", description = "Soft delete a Plan of Care")
    public ResponseEntity<Map<String, String>> deletePOC(@PathVariable Long id) {
        pocService.deletePOC(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Plan of Care deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to check if user has a clinician role
     */
    private boolean hasClinicianRole(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream()
                .anyMatch(auth -> {
                    String authority = auth.getAuthority();
                    return authority.equals("RN") || 
                           authority.equals("PT") || 
                           authority.equals("OT") || 
                           authority.equals("ST") || 
                           authority.equals("HHA");
                });
    }
}

