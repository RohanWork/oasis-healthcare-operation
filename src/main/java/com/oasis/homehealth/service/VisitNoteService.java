package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.VisitNoteDTO;
import com.oasis.homehealth.dto.VisitNoteRequest;
import com.oasis.homehealth.entity.*;
import com.oasis.homehealth.repository.*;
import com.oasis.homehealth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitNoteService {

    private final VisitNoteRepository visitNoteRepository;
    private final TaskRepository taskRepository;
    private final PatientRepository patientRepository;
    private final EpisodeRepository episodeRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    /**
     * Create a new visit note
     */
    @Transactional
    public VisitNoteDTO createVisitNote(VisitNoteRequest request, Long organizationId) {
        log.info("Creating visit note for task: {}", request.getTaskId());

        // Validate organization
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Validate task
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Validate patient
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Validate episode
        Episode episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        // Get current user (clinician)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User clinician = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if visit note already exists for this task
        visitNoteRepository.findByTaskId(request.getTaskId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Visit note already exists for this task");
                });

        // Create visit note
        VisitNote visitNote = VisitNote.builder()
                .task(task)
                .patient(patient)
                .episode(episode)
                .clinician(clinician)
                .organization(organization)
                .visitType(request.getVisitType())
                .visitDate(request.getVisitDate())
                .visitStartTime(request.getVisitStartTime())
                .visitEndTime(request.getVisitEndTime())
                .visitDurationMinutes(request.getVisitDurationMinutes())
                .chiefComplaint(request.getChiefComplaint())
                .vitalSigns(request.getVitalSigns())
                .assessmentFindings(request.getAssessmentFindings())
                .interventionsProvided(request.getInterventionsProvided())
                .patientResponse(request.getPatientResponse())
                .teachingProvided(request.getTeachingProvided())
                .followUpPlan(request.getFollowUpPlan())
                .nextVisitDate(request.getNextVisitDate())
                .mileage(request.getMileage())
                .travelTimeMinutes(request.getTravelTimeMinutes())
                .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                .specialNotes(request.getSpecialNotes())
                .physicianContacted(request.getPhysicianContacted())
                .physicianContactReason(request.getPhysicianContactReason())
                .revisionNumber(0)
                .build();

        // Calculate duration if times are provided
        if (visitNote.getVisitStartTime() != null && visitNote.getVisitEndTime() != null) {
            visitNote.calculateVisitDuration();
        }

        visitNote = visitNoteRepository.save(visitNote);

        log.info("Visit note created: {}", visitNote.getId());
        return convertToDTO(visitNote);
    }

    /**
     * Update an existing visit note
     */
    @Transactional
    public VisitNoteDTO updateVisitNote(Long id, VisitNoteRequest request, Long organizationId) {
        log.info("Updating visit note: {}", id);

        VisitNote visitNote = visitNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit note not found"));

        // Verify organization
        if (!visitNote.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        // Check if can be edited
        if (!visitNote.canBeEdited()) {
            throw new RuntimeException("Visit note cannot be edited. Status: " + visitNote.getStatus());
        }

        // Update fields
        visitNote.setVisitDate(request.getVisitDate());
        visitNote.setVisitStartTime(request.getVisitStartTime());
        visitNote.setVisitEndTime(request.getVisitEndTime());
        visitNote.setVisitDurationMinutes(request.getVisitDurationMinutes());
        visitNote.setChiefComplaint(request.getChiefComplaint());
        visitNote.setVitalSigns(request.getVitalSigns());
        visitNote.setAssessmentFindings(request.getAssessmentFindings());
        visitNote.setInterventionsProvided(request.getInterventionsProvided());
        visitNote.setPatientResponse(request.getPatientResponse());
        visitNote.setTeachingProvided(request.getTeachingProvided());
        visitNote.setFollowUpPlan(request.getFollowUpPlan());
        visitNote.setNextVisitDate(request.getNextVisitDate());
        visitNote.setMileage(request.getMileage());
        visitNote.setTravelTimeMinutes(request.getTravelTimeMinutes());
        visitNote.setSpecialNotes(request.getSpecialNotes());
        visitNote.setPhysicianContacted(request.getPhysicianContacted());
        visitNote.setPhysicianContactReason(request.getPhysicianContactReason());

        // Calculate duration if times are provided
        if (visitNote.getVisitStartTime() != null && visitNote.getVisitEndTime() != null) {
            visitNote.calculateVisitDuration();
        }

        // Increment revision number
        if (visitNote.getRevisionNumber() == null) {
            visitNote.setRevisionNumber(1);
        } else {
            visitNote.setRevisionNumber(visitNote.getRevisionNumber() + 1);
        }

        visitNote = visitNoteRepository.save(visitNote);

        log.info("Visit note updated: {}", id);
        return convertToDTO(visitNote);
    }

    /**
     * Submit visit note for QA review
     */
    @Transactional
    public VisitNoteDTO submitForQA(Long id, Long organizationId) {
        log.info("Submitting visit note for QA: {}", id);

        VisitNote visitNote = visitNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit note not found"));

        // Verify organization
        if (!visitNote.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        // Check if can be submitted
        if (!visitNote.canBeEdited()) {
            throw new RuntimeException("Visit note cannot be submitted. Status: " + visitNote.getStatus());
        }

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User submittedBy = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        visitNote.setStatus("SUBMITTED");
        visitNote.setSubmittedBy(submittedBy);
        visitNote.setSubmittedAt(LocalDateTime.now());

        visitNote = visitNoteRepository.save(visitNote);

        log.info("Visit note submitted for QA: {}", id);
        return convertToDTO(visitNote);
    }

    /**
     * QA Review - Approve visit note
     */
    @Transactional
    public VisitNoteDTO approveVisitNote(Long id, String qaComments, Long organizationId) {
        log.info("Approving visit note: {}", id);

        VisitNote visitNote = visitNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit note not found"));

        // Verify organization
        if (!visitNote.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        if (!visitNote.needsQAReview()) {
            throw new RuntimeException("Visit note is not pending QA review");
        }

        // Get current user (QA reviewer)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User reviewedBy = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        visitNote.setStatus("APPROVED");
        visitNote.setReviewedBy(reviewedBy);
        visitNote.setReviewedAt(LocalDateTime.now());
        visitNote.setQaComments(qaComments);

        // Update task status to completed
        Task task = visitNote.getTask();
        if (task != null) {
            task.setStatus("COMPLETED");
            task.setActualEndTime(LocalDateTime.now());
            taskRepository.save(task);
        }

        visitNote = visitNoteRepository.save(visitNote);

        log.info("Visit note approved: {}", id);
        return convertToDTO(visitNote);
    }

    /**
     * QA Review - Return for correction
     */
    @Transactional
    public VisitNoteDTO returnForCorrection(Long id, String correctionComments, Long organizationId) {
        log.info("Returning visit note for correction: {}", id);

        VisitNote visitNote = visitNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit note not found"));

        // Verify organization
        if (!visitNote.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        if (!visitNote.needsQAReview()) {
            throw new RuntimeException("Visit note is not pending QA review");
        }

        // Get current user (QA reviewer)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User reviewedBy = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        visitNote.setStatus("RETURNED");
        visitNote.setReviewedBy(reviewedBy);
        visitNote.setReviewedAt(LocalDateTime.now());
        visitNote.setCorrectionComments(correctionComments);
        visitNote.setReturnedForCorrectionAt(LocalDateTime.now());

        visitNote = visitNoteRepository.save(visitNote);

        log.info("Visit note returned for correction: {}", id);
        return convertToDTO(visitNote);
    }

    /**
     * Get visit note by ID
     */
    @Transactional(readOnly = true)
    public VisitNoteDTO getVisitNoteById(Long id, Long organizationId) {
        VisitNote visitNote = visitNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit note not found"));

        // Verify organization
        if (!visitNote.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        return convertToDTO(visitNote);
    }

    /**
     * Get visit note by task ID
     */
    @Transactional(readOnly = true)
    public VisitNoteDTO getVisitNoteByTaskId(Long taskId, Long organizationId) {
        VisitNote visitNote = visitNoteRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Visit note not found for this task"));

        // Verify organization
        if (!visitNote.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        return convertToDTO(visitNote);
    }

    /**
     * Get all visit notes for organization
     */
    @Transactional(readOnly = true)
    public List<VisitNoteDTO> getAllVisitNotes(Long organizationId) {
        return visitNoteRepository.findByOrganizationIdAndDateRange(
                organizationId, LocalDate.of(2000, 1, 1), LocalDate.of(2100, 12, 31))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get visit notes by patient
     */
    @Transactional(readOnly = true)
    public List<VisitNoteDTO> getVisitNotesByPatient(Long patientId, Long organizationId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (!patient.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        return visitNoteRepository.findByPatientId(patientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get visit notes by episode
     */
    @Transactional(readOnly = true)
    public List<VisitNoteDTO> getVisitNotesByEpisode(Long episodeId, Long organizationId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        if (!episode.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        return visitNoteRepository.findByEpisodeId(episodeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get visit notes by clinician
     */
    @Transactional(readOnly = true)
    public List<VisitNoteDTO> getVisitNotesByClinician(Long clinicianId, Long organizationId) {
        User clinician = userRepository.findById(clinicianId)
                .orElseThrow(() -> new RuntimeException("Clinician not found"));

        return visitNoteRepository.findByClinicianId(clinicianId)
                .stream()
                .filter(vn -> vn.getOrganization().getId().equals(organizationId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending QA review visit notes
     */
    @Transactional(readOnly = true)
    public List<VisitNoteDTO> getPendingQAReview(Long organizationId) {
        // If organizationId is null, get all pending reviews (for SYSTEM_ADMIN)
        if (organizationId == null) {
            return visitNoteRepository.findAllPendingQAReview()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        return visitNoteRepository.findPendingQAReview(organizationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get visit notes by status
     */
    @Transactional(readOnly = true)
    public List<VisitNoteDTO> getVisitNotesByStatus(String status, Long organizationId) {
        return visitNoteRepository.findByOrganizationIdAndStatus(organizationId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete visit note (soft delete)
     */
    @Transactional
    public void deleteVisitNote(Long id, Long organizationId) {
        log.info("Deleting visit note: {}", id);

        VisitNote visitNote = visitNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit note not found"));

        // Verify organization
        if (!visitNote.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }

        visitNote.setIsDeleted(true);
        visitNoteRepository.save(visitNote);

        log.info("Visit note deleted: {}", id);
    }

    /**
     * Convert VisitNote entity to DTO
     */
    private VisitNoteDTO convertToDTO(VisitNote visitNote) {
        return VisitNoteDTO.builder()
                .id(visitNote.getId())
                .taskId(visitNote.getTask() != null ? visitNote.getTask().getId() : null)
                .taskNumber(visitNote.getTask() != null ? visitNote.getTask().getTaskNumber() : null)
                .patientId(visitNote.getPatient() != null ? visitNote.getPatient().getId() : null)
                .patientName(visitNote.getPatient() != null ? visitNote.getPatient().getFullName() : null)
                .episodeId(visitNote.getEpisode() != null ? visitNote.getEpisode().getId() : null)
                .episodeNumber(visitNote.getEpisode() != null ? visitNote.getEpisode().getEpisodeNumber() : null)
                .clinicianId(visitNote.getClinician() != null ? visitNote.getClinician().getId() : null)
                .clinicianName(visitNote.getClinician() != null ? visitNote.getClinician().getFullName() : null)
                .organizationId(visitNote.getOrganization() != null ? visitNote.getOrganization().getId() : null)
                .organizationName(visitNote.getOrganization() != null ? visitNote.getOrganization().getOrganizationName() : null)
                .visitType(visitNote.getVisitType())
                .visitDate(visitNote.getVisitDate())
                .visitStartTime(visitNote.getVisitStartTime())
                .visitEndTime(visitNote.getVisitEndTime())
                .visitDurationMinutes(visitNote.getVisitDurationMinutes())
                .chiefComplaint(visitNote.getChiefComplaint())
                .vitalSigns(visitNote.getVitalSigns())
                .assessmentFindings(visitNote.getAssessmentFindings())
                .interventionsProvided(visitNote.getInterventionsProvided())
                .patientResponse(visitNote.getPatientResponse())
                .teachingProvided(visitNote.getTeachingProvided())
                .followUpPlan(visitNote.getFollowUpPlan())
                .nextVisitDate(visitNote.getNextVisitDate())
                .mileage(visitNote.getMileage())
                .travelTimeMinutes(visitNote.getTravelTimeMinutes())
                .status(visitNote.getStatus())
                .submittedById(visitNote.getSubmittedBy() != null ? visitNote.getSubmittedBy().getId() : null)
                .submittedByName(visitNote.getSubmittedBy() != null ? visitNote.getSubmittedBy().getFullName() : null)
                .submittedAt(visitNote.getSubmittedAt())
                .reviewedById(visitNote.getReviewedBy() != null ? visitNote.getReviewedBy().getId() : null)
                .reviewedByName(visitNote.getReviewedBy() != null ? visitNote.getReviewedBy().getFullName() : null)
                .reviewedAt(visitNote.getReviewedAt())
                .qaComments(visitNote.getQaComments())
                .returnedForCorrectionAt(visitNote.getReturnedForCorrectionAt())
                .correctionComments(visitNote.getCorrectionComments())
                .revisionNumber(visitNote.getRevisionNumber())
                .specialNotes(visitNote.getSpecialNotes())
                .physicianContacted(visitNote.getPhysicianContacted())
                .physicianContactReason(visitNote.getPhysicianContactReason())
                .createdAt(visitNote.getCreatedAt())
                .updatedAt(visitNote.getUpdatedAt())
                .createdByName(visitNote.getCreatedBy())
                .updatedByName(visitNote.getUpdatedBy())
                .build();
    }
}

