package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.VisitNoteDTO;
import com.oasis.homehealth.dto.VisitNoteRequest;
import com.oasis.homehealth.service.VisitNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.oasis.homehealth.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/visit-notes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Visit Notes", description = "Visit Note Management APIs")
public class VisitNoteController {

    private final VisitNoteService visitNoteService;

    /**
     * Create a new visit note
     */
    @PostMapping
    @PreAuthorize("hasAuthority('VISIT_CREATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ST', 'ROLE_HHA')")
    @Operation(summary = "Create visit note", description = "Create a new visit note for a task")
    public ResponseEntity<VisitNoteDTO> createVisitNote(
            @Valid @RequestBody VisitNoteRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to create visit note for task: {}", request.getTaskId());
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        VisitNoteDTO visitNote = visitNoteService.createVisitNote(request, organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(visitNote);
    }

    /**
     * Update an existing visit note
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VISIT_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ST', 'ROLE_HHA')")
    @Operation(summary = "Update visit note", description = "Update an existing visit note")
    public ResponseEntity<VisitNoteDTO> updateVisitNote(
            @PathVariable Long id,
            @Valid @RequestBody VisitNoteRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to update visit note: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        VisitNoteDTO visitNote = visitNoteService.updateVisitNote(id, request, organizationId);
        return ResponseEntity.ok(visitNote);
    }

    /**
     * Get visit note by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ST', 'ROLE_HHA', 'ROLE_QA_NURSE')")
    @Operation(summary = "Get visit note by ID", description = "Retrieve a visit note by its ID")
    public ResponseEntity<VisitNoteDTO> getVisitNoteById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to get visit note: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        VisitNoteDTO visitNote = visitNoteService.getVisitNoteById(id, organizationId);
        return ResponseEntity.ok(visitNote);
    }

    /**
     * Get visit note by task ID
     */
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ST', 'ROLE_HHA', 'ROLE_QA_NURSE')")
    @Operation(summary = "Get visit note by task ID", description = "Retrieve a visit note by task ID")
    public ResponseEntity<VisitNoteDTO> getVisitNoteByTaskId(
            @PathVariable Long taskId,
            HttpServletRequest httpRequest) {
        log.info("REST request to get visit note for task: {}", taskId);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        VisitNoteDTO visitNote = visitNoteService.getVisitNoteByTaskId(taskId, organizationId);
        return ResponseEntity.ok(visitNote);
    }

    /**
     * Get all visit notes
     */
    @GetMapping
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_QA_NURSE')")
    @Operation(summary = "Get all visit notes", description = "Retrieve all visit notes for the organization")
    public ResponseEntity<List<VisitNoteDTO>> getAllVisitNotes(HttpServletRequest httpRequest) {
        log.info("REST request to get all visit notes");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<VisitNoteDTO> visitNotes = visitNoteService.getAllVisitNotes(organizationId);
        return ResponseEntity.ok(visitNotes);
    }

    /**
     * Get visit notes by patient
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ST', 'ROLE_HHA', 'ROLE_QA_NURSE')")
    @Operation(summary = "Get visit notes by patient", description = "Retrieve all visit notes for a specific patient")
    public ResponseEntity<List<VisitNoteDTO>> getVisitNotesByPatient(
            @PathVariable Long patientId,
            HttpServletRequest httpRequest) {
        log.info("REST request to get visit notes for patient: {}", patientId);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<VisitNoteDTO> visitNotes = visitNoteService.getVisitNotesByPatient(patientId, organizationId);
        return ResponseEntity.ok(visitNotes);
    }

    /**
     * Get visit notes by episode
     */
    @GetMapping("/episode/{episodeId}")
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ST', 'ROLE_HHA', 'ROLE_QA_NURSE')")
    @Operation(summary = "Get visit notes by episode", description = "Retrieve all visit notes for a specific episode")
    public ResponseEntity<List<VisitNoteDTO>> getVisitNotesByEpisode(
            @PathVariable Long episodeId,
            HttpServletRequest httpRequest) {
        log.info("REST request to get visit notes for episode: {}", episodeId);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<VisitNoteDTO> visitNotes = visitNoteService.getVisitNotesByEpisode(episodeId, organizationId);
        return ResponseEntity.ok(visitNotes);
    }

    /**
     * Get visit notes by clinician
     */
    @GetMapping("/clinician/{clinicianId}")
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Get visit notes by clinician", description = "Retrieve all visit notes for a specific clinician")
    public ResponseEntity<List<VisitNoteDTO>> getVisitNotesByClinician(
            @PathVariable Long clinicianId,
            HttpServletRequest httpRequest) {
        log.info("REST request to get visit notes for clinician: {}", clinicianId);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<VisitNoteDTO> visitNotes = visitNoteService.getVisitNotesByClinician(clinicianId, organizationId);
        return ResponseEntity.ok(visitNotes);
    }

    /**
     * Get pending QA review visit notes
     */
    @GetMapping("/qa/pending")
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_QA_NURSE', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Get pending QA review visit notes", description = "Retrieve all visit notes pending QA review")
    public ResponseEntity<List<VisitNoteDTO>> getPendingQAReview(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("REST request to get pending QA review visit notes");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // SYSTEM_ADMIN can access all pending reviews without organization context
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
        
        if (organizationId == null && !isSystemAdmin) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        
        List<VisitNoteDTO> visitNotes = visitNoteService.getPendingQAReview(organizationId);
        return ResponseEntity.ok(visitNotes);
    }

    /**
     * Get visit notes by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('VISIT_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER', 'ROLE_QA_NURSE')")
    @Operation(summary = "Get visit notes by status", description = "Retrieve all visit notes with a specific status")
    public ResponseEntity<List<VisitNoteDTO>> getVisitNotesByStatus(
            @PathVariable String status,
            HttpServletRequest httpRequest) {
        log.info("REST request to get visit notes with status: {}", status);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<VisitNoteDTO> visitNotes = visitNoteService.getVisitNotesByStatus(status, organizationId);
        return ResponseEntity.ok(visitNotes);
    }

    /**
     * Submit visit note for QA review
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('VISIT_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_RN', 'ROLE_PT', 'ROLE_OT', 'ROLE_ST', 'ROLE_HHA')")
    @Operation(summary = "Submit visit note for QA", description = "Submit a visit note for QA review")
    public ResponseEntity<VisitNoteDTO> submitForQA(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to submit visit note for QA: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        VisitNoteDTO visitNote = visitNoteService.submitForQA(id, organizationId);
        return ResponseEntity.ok(visitNote);
    }

    /**
     * QA Review - Approve visit note
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('VISIT_APPROVE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_QA_NURSE', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Approve visit note", description = "Approve a visit note after QA review")
    public ResponseEntity<VisitNoteDTO> approveVisitNote(
            @PathVariable Long id,
            @RequestParam(required = false) String qaComments,
            HttpServletRequest httpRequest) {
        log.info("REST request to approve visit note: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        VisitNoteDTO visitNote = visitNoteService.approveVisitNote(id, qaComments, organizationId);
        return ResponseEntity.ok(visitNote);
    }

    /**
     * QA Review - Return for correction
     */
    @PostMapping("/{id}/return")
    @PreAuthorize("hasAuthority('VISIT_APPROVE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_QA_NURSE', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Return visit note for correction", description = "Return a visit note to the clinician for correction")
    public ResponseEntity<VisitNoteDTO> returnForCorrection(
            @PathVariable Long id,
            @RequestParam String correctionComments,
            HttpServletRequest httpRequest) {
        log.info("REST request to return visit note for correction: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        VisitNoteDTO visitNote = visitNoteService.returnForCorrection(id, correctionComments, organizationId);
        return ResponseEntity.ok(visitNote);
    }

    /**
     * Delete visit note
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VISIT_DELETE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Delete visit note", description = "Soft delete a visit note")
    public ResponseEntity<Void> deleteVisitNote(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("REST request to delete visit note: {}", id);
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        visitNoteService.deleteVisitNote(id, organizationId);
        return ResponseEntity.noContent().build();
    }
}

