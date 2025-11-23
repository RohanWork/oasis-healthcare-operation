package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.OasisAssessmentCompleteDTO;
import com.oasis.homehealth.dto.OasisAssessmentCompleteRequest;
import com.oasis.homehealth.dto.OasisQARequest;
import com.oasis.homehealth.service.OasisAssessmentCompleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Complete OASIS-E1 Assessment Management
 * Handles all 300+ fields from official CMS OASIS-E1 document
 */
@RestController
@RequestMapping("/oasis-complete")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class OasisAssessmentCompleteController {

    private final OasisAssessmentCompleteService oasisService;

    /**
     * Create new OASIS assessment
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_QA_NURSE', 'ROLE_ORG_ADMIN', 'ROLE_INTAKE_COORDINATOR', 'ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<OasisAssessmentCompleteDTO> createAssessment(
            @Valid @RequestBody OasisAssessmentCompleteRequest request) {
        log.info("Creating complete OASIS-E1 assessment for patient: {}", request.getPatientId());
        OasisAssessmentCompleteDTO assessment = oasisService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assessment);
    }

    /**
     * Update existing assessment
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ORG_ADMIN', 'ROLE_INTAKE_COORDINATOR', 'ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<OasisAssessmentCompleteDTO> updateAssessment(
            @PathVariable Long id,
            @Valid @RequestBody OasisAssessmentCompleteRequest request) {
        log.info("Updating complete OASIS-E1 assessment: {}", id);
        OasisAssessmentCompleteDTO assessment = oasisService.updateAssessment(id, request);
        return ResponseEntity.ok(assessment);
    }

    /**
     * Auto-save assessment (called every 15 seconds)
     */
    @PostMapping("/{id}/auto-save")
    @PreAuthorize("hasAnyRole('ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ORG_ADMIN', 'ROLE_INTAKE_COORDINATOR', 'ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<OasisAssessmentCompleteDTO> autoSaveAssessment(
            @PathVariable Long id,
            @Valid @RequestBody OasisAssessmentCompleteRequest request) {
        log.debug("Auto-saving complete OASIS-E1 assessment: {}", id);
        OasisAssessmentCompleteDTO assessment = oasisService.autoSaveAssessment(id, request);
        return ResponseEntity.ok(assessment);
    }

    /**
     * Get assessment by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_INTAKE_COORDINATOR')")
    public ResponseEntity<OasisAssessmentCompleteDTO> getAssessment(@PathVariable Long id) {
        log.info("Fetching complete OASIS-E1 assessment: {}", id);
        OasisAssessmentCompleteDTO assessment = oasisService.getAssessment(id);
        return ResponseEntity.ok(assessment);
    }

    /**
     * Get all assessments for a patient
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_INTAKE_COORDINATOR')")
    public ResponseEntity<List<OasisAssessmentCompleteDTO>> getAssessmentsByPatient(
            @PathVariable Long patientId) {
        log.info("Fetching complete OASIS-E1 assessments for patient: {}", patientId);
        List<OasisAssessmentCompleteDTO> assessments = oasisService.getAssessmentsByPatient(patientId);
        return ResponseEntity.ok(assessments);
    }

    /**
     * Submit assessment for QA review
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ORG_ADMIN', 'ROLE_INTAKE_COORDINATOR', 'ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<OasisAssessmentCompleteDTO> submitForQA(@PathVariable Long id) {
        log.info("Submitting complete OASIS-E1 assessment for QA: {}", id);
        OasisAssessmentCompleteDTO assessment = oasisService.submitForQA(id);
        return ResponseEntity.ok(assessment);
    }

    /**
     * QA Review - Approve or Reject
     */
    @PostMapping("/qa/review")
    @PreAuthorize("hasAnyRole('ROLE_CLINICAL_MANAGER', 'ROLE_QA_NURSE', 'ROLE_ORG_ADMIN', 'ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<OasisAssessmentCompleteDTO> reviewAssessment(
            @Valid @RequestBody OasisQARequest request) {
        log.info("QA reviewing complete OASIS-E1 assessment: {}", request.getAssessmentId());
        OasisAssessmentCompleteDTO assessment = oasisService.reviewAssessment(request);
        return ResponseEntity.ok(assessment);
    }

    /**
     * Delete assessment (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER')")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        log.info("Deleting complete OASIS-E1 assessment: {}", id);
        oasisService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OASIS-E1 Complete Service is running - 300+ fields ready!");
    }
}

