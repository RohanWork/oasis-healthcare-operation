package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.PatientDTO;
import com.oasis.homehealth.dto.PatientRequest;
import com.oasis.homehealth.security.UserPrincipal;
import com.oasis.homehealth.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient Management", description = "APIs for managing patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT_CREATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_INTAKE_COORDINATOR', 'ROLE_QA_NURSE')")
    @Operation(summary = "Create new patient", description = "Create a new patient record")
    public ResponseEntity<PatientDTO> createPatient(
            @Valid @RequestBody PatientRequest request,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        if (organizationId == null) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        PatientDTO patient = patientService.createPatient(request, organizationId);
        return new ResponseEntity<>(patient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    @Operation(summary = "Update patient", description = "Update an existing patient record")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        PatientDTO patient = patientService.updatePatient(id, request);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    @Operation(summary = "Get patient by ID", description = "Retrieve patient details by ID")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    @Operation(summary = "Get all patients", description = "Retrieve all patients based on user role and organization")
    public ResponseEntity<List<PatientDTO>> getAllPatients(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // Role-based filtering: RN, PT, OT, ST, HHA see only their assigned patients
        if (hasClinicianRole(currentUser)) {
            List<PatientDTO> patients = patientService.getPatientsByOrganizationAndClinician(organizationId, currentUser.getId());
            return ResponseEntity.ok(patients);
        }
        
        // Admin, Intake Coordinator, Scheduler, Billing see all patients in organization
        List<PatientDTO> patients = patientService.getAllPatientsByOrganization(organizationId);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    @Operation(summary = "Search patients", description = "Search patients by name or MRN based on user role")
    public ResponseEntity<List<PatientDTO>> searchPatients(
            @RequestParam String searchTerm,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // Role-based filtering: RN, PT, OT, ST, HHA search only their assigned patients
        if (hasClinicianRole(currentUser)) {
            List<PatientDTO> patients = patientService.searchPatientsByOrganizationAndClinician(
                organizationId, currentUser.getId(), searchTerm);
            return ResponseEntity.ok(patients);
        }
        
        // Admin, Intake Coordinator, Scheduler, Billing search all patients in organization
        List<PatientDTO> patients = patientService.searchPatients(organizationId, searchTerm);
        return ResponseEntity.ok(patients);
    }
    
    /**
     * Helper method to check if user has a clinician role (RN, PT, OT, ST, HHA)
     * Clinicians should only see patients assigned to them
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

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    @Operation(summary = "Get patients by status", description = "Retrieve patients by status")
    public ResponseEntity<List<PatientDTO>> getPatientsByStatus(
            @PathVariable String status,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<PatientDTO> patients = patientService.getPatientsByStatus(organizationId, status);
        return ResponseEntity.ok(patients);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_DELETE')")
    @Operation(summary = "Delete patient", description = "Soft delete a patient record")
    public ResponseEntity<Map<String, String>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    @Operation(summary = "Get patient statistics", description = "Get patient counts by status")
    public ResponseEntity<Map<String, Long>> getPatientStats(HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        Map<String, Long> stats = new HashMap<>();
        stats.put("activePatients", patientService.countActivePatients(organizationId));
        stats.put("pendingPatients", patientService.countPendingPatients(organizationId));
        return ResponseEntity.ok(stats);
    }
}

