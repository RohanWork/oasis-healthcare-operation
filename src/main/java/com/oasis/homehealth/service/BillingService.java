package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.BillingClaimDTO;
import com.oasis.homehealth.dto.BillingClaimRequest;
import com.oasis.homehealth.entity.*;
import com.oasis.homehealth.repository.*;
import com.oasis.homehealth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingService {

    private final BillingClaimRepository billingClaimRepository;
    private final PatientRepository patientRepository;
    private final EpisodeRepository episodeRepository;
    private final OrganizationRepository organizationRepository;
    private final VisitNoteRepository visitNoteRepository;
    private final TaskRepository taskRepository;
    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;

    /**
     * Create a new billing claim
     */
    public BillingClaimDTO createClaim(BillingClaimRequest request, Long organizationId) {
        log.info("Creating billing claim for patient: {}", request.getPatientId());

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        if (!patient.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Patient does not belong to your organization.");
        }

        Episode episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        if (!episode.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Episode does not belong to your organization.");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Generate claim number
        String claimNumber = generateClaimNumber(organization, request.getClaimType());

        // Calculate total charge
        BigDecimal totalCharge = request.getUnitRate().multiply(BigDecimal.valueOf(request.getUnits()));

        BillingClaim claim = BillingClaim.builder()
                .patient(patient)
                .episode(episode)
                .organization(organization)
                .claimNumber(claimNumber)
                .claimType(request.getClaimType())
                .billingDate(request.getBillingDate())
                .serviceDate(request.getServiceDate())
                .serviceEndDate(request.getServiceEndDate())
                .serviceCode(request.getServiceCode())
                .serviceDescription(request.getServiceDescription())
                .units(request.getUnits())
                .unitRate(request.getUnitRate())
                .totalCharge(totalCharge)
                .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                .patientResponsibility(request.getPatientResponsibility())
                .adjustmentAmount(request.getAdjustmentAmount())
                .adjustmentReason(request.getAdjustmentReason())
                .notes(request.getNotes())
                .build();

        // Link visit note if provided
        if (request.getVisitNoteId() != null) {
            VisitNote visitNote = visitNoteRepository.findById(request.getVisitNoteId())
                    .orElseThrow(() -> new RuntimeException("Visit note not found"));
            claim.setVisitNote(visitNote);
        }

        // Link task if provided
        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            claim.setTask(task);
        }

        // Link insurance if provided
        if (request.getInsuranceId() != null) {
            Insurance insurance = insuranceRepository.findById(request.getInsuranceId())
                    .orElseThrow(() -> new RuntimeException("Insurance not found"));
            claim.setInsurance(insurance);
        }

        claim.setInsuranceClaimNumber(request.getInsuranceClaimNumber());

        // Set billed by
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            claim.setBilledByUserId(userPrincipal.getId());
            claim.setBilledAt(LocalDateTime.now());
        }

        claim = billingClaimRepository.save(claim);
        log.info("Billing claim created with ID: {}", claim.getId());
        return convertToDTO(claim);
    }

    /**
     * Update an existing billing claim
     */
    public BillingClaimDTO updateClaim(Long id, BillingClaimRequest request, Long organizationId) {
        log.info("Updating billing claim: {}", id);

        BillingClaim claim = billingClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing claim not found"));

        if (!claim.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Claim does not belong to your organization.");
        }

        if (!claim.canBeEdited()) {
            throw new IllegalStateException("Cannot update a claim that is not in DRAFT or DENIED status.");
        }

        // Update fields
        claim.setClaimType(request.getClaimType());
        claim.setBillingDate(request.getBillingDate());
        claim.setServiceDate(request.getServiceDate());
        claim.setServiceEndDate(request.getServiceEndDate());
        claim.setServiceCode(request.getServiceCode());
        claim.setServiceDescription(request.getServiceDescription());
        claim.setUnits(request.getUnits());
        claim.setUnitRate(request.getUnitRate());
        claim.setTotalCharge(request.getUnitRate().multiply(BigDecimal.valueOf(request.getUnits())));
        claim.setStatus(request.getStatus() != null ? request.getStatus() : claim.getStatus());
        claim.setPatientResponsibility(request.getPatientResponsibility());
        claim.setAdjustmentAmount(request.getAdjustmentAmount());
        claim.setAdjustmentReason(request.getAdjustmentReason());
        claim.setNotes(request.getNotes());
        claim.setInsuranceClaimNumber(request.getInsuranceClaimNumber());

        // Update insurance if provided
        if (request.getInsuranceId() != null) {
            Insurance insurance = insuranceRepository.findById(request.getInsuranceId())
                    .orElseThrow(() -> new RuntimeException("Insurance not found"));
            claim.setInsurance(insurance);
        }

        claim = billingClaimRepository.save(claim);
        log.info("Billing claim updated: {}", id);
        return convertToDTO(claim);
    }

    /**
     * Get billing claim by ID
     */
    @Transactional(readOnly = true)
    public BillingClaimDTO getClaimById(Long id, Long organizationId) {
        BillingClaim claim = billingClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing claim not found"));

        if (!claim.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Claim does not belong to your organization.");
        }

        return convertToDTO(claim);
    }

    /**
     * Get all billing claims for organization
     */
    @Transactional(readOnly = true)
    public List<BillingClaimDTO> getAllClaims(Long organizationId) {
        return billingClaimRepository.findByOrganizationId(organizationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get billing claims by patient
     */
    @Transactional(readOnly = true)
    public List<BillingClaimDTO> getClaimsByPatient(Long patientId, Long organizationId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        if (!patient.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Patient does not belong to your organization.");
        }
        return billingClaimRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get billing claims by episode
     */
    @Transactional(readOnly = true)
    public List<BillingClaimDTO> getClaimsByEpisode(Long episodeId, Long organizationId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        if (!episode.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Episode does not belong to your organization.");
        }
        return billingClaimRepository.findByEpisodeId(episodeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get billing claims by status
     */
    @Transactional(readOnly = true)
    public List<BillingClaimDTO> getClaimsByStatus(String status, Long organizationId) {
        return billingClaimRepository.findByOrganizationIdAndStatus(organizationId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Submit claim for processing
     */
    public BillingClaimDTO submitClaim(Long id, Long organizationId) {
        log.info("Submitting billing claim: {}", id);
        BillingClaim claim = billingClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing claim not found"));

        if (!claim.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Claim does not belong to your organization.");
        }

        if (!"DRAFT".equals(claim.getStatus())) {
            throw new IllegalStateException("Only DRAFT claims can be submitted.");
        }

        claim.setStatus("SUBMITTED");
        claim.setSubmittedDate(LocalDate.now());
        claim = billingClaimRepository.save(claim);

        log.info("Billing claim submitted: {}", id);
        return convertToDTO(claim);
    }

    /**
     * Mark claim as paid
     */
    public BillingClaimDTO markAsPaid(Long id, BigDecimal paidAmount, Long organizationId) {
        log.info("Marking billing claim as paid: {}", id);
        BillingClaim claim = billingClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing claim not found"));

        if (!claim.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Claim does not belong to your organization.");
        }

        claim.setStatus("PAID");
        claim.setPaidDate(LocalDate.now());
        claim.setPaidAmount(paidAmount);
        claim.setInsurancePayment(paidAmount);
        claim = billingClaimRepository.save(claim);

        log.info("Billing claim marked as paid: {}", id);
        return convertToDTO(claim);
    }

    /**
     * Mark claim as denied
     */
    public BillingClaimDTO markAsDenied(Long id, String denialReason, Long organizationId) {
        log.info("Marking billing claim as denied: {}", id);
        BillingClaim claim = billingClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing claim not found"));

        if (!claim.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Claim does not belong to your organization.");
        }

        claim.setStatus("DENIED");
        claim.setDeniedDate(LocalDate.now());
        claim.setDenialReason(denialReason);
        claim = billingClaimRepository.save(claim);

        log.info("Billing claim marked as denied: {}", id);
        return convertToDTO(claim);
    }

    /**
     * Delete billing claim (soft delete)
     */
    public void deleteClaim(Long id, Long organizationId) {
        log.info("Deleting billing claim: {}", id);
        BillingClaim claim = billingClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing claim not found"));

        if (!claim.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Claim does not belong to your organization.");
        }

        if (!claim.canBeEdited()) {
            throw new IllegalStateException("Cannot delete a claim that is not in DRAFT or DENIED status.");
        }

        claim.setIsDeleted(true);
        billingClaimRepository.save(claim);
        log.info("Billing claim deleted: {}", id);
    }

    /**
     * Generate claim number
     */
    private String generateClaimNumber(Organization organization, String claimType) {
        String prefix = organization.getOrganizationCode().replaceAll("[^A-Z0-9]", "").toUpperCase();
        String typePrefix = claimType.substring(0, Math.min(3, claimType.length())).toUpperCase();
        long count = billingClaimRepository.count() + 1;
        return String.format("%s-%s-%06d", prefix, typePrefix, count);
    }

    /**
     * Convert entity to DTO
     */
    private BillingClaimDTO convertToDTO(BillingClaim claim) {
        User billedBy = claim.getBilledByUserId() != null ?
                userRepository.findById(claim.getBilledByUserId()).orElse(null) : null;

        return BillingClaimDTO.builder()
                .id(claim.getId())
                .patientId(claim.getPatient() != null ? claim.getPatient().getId() : null)
                .patientName(claim.getPatient() != null ? claim.getPatient().getFullName() : null)
                .episodeId(claim.getEpisode() != null ? claim.getEpisode().getId() : null)
                .episodeNumber(claim.getEpisode() != null ? claim.getEpisode().getEpisodeNumber() : null)
                .organizationId(claim.getOrganization() != null ? claim.getOrganization().getId() : null)
                .organizationName(claim.getOrganization() != null ? claim.getOrganization().getOrganizationName() : null)
                .visitNoteId(claim.getVisitNote() != null ? claim.getVisitNote().getId() : null)
                .taskId(claim.getTask() != null ? claim.getTask().getId() : null)
                .claimNumber(claim.getClaimNumber())
                .claimType(claim.getClaimType())
                .billingDate(claim.getBillingDate())
                .serviceDate(claim.getServiceDate())
                .serviceEndDate(claim.getServiceEndDate())
                .serviceCode(claim.getServiceCode())
                .serviceDescription(claim.getServiceDescription())
                .units(claim.getUnits())
                .unitRate(claim.getUnitRate())
                .totalCharge(claim.getTotalCharge())
                .insuranceId(claim.getInsurance() != null ? claim.getInsurance().getId() : null)
                .insuranceName(claim.getInsurance() != null ? claim.getInsurance().getInsuranceCompany() : null)
                .insuranceClaimNumber(claim.getInsuranceClaimNumber())
                .status(claim.getStatus())
                .submittedDate(claim.getSubmittedDate())
                .paidDate(claim.getPaidDate())
                .paidAmount(claim.getPaidAmount())
                .deniedDate(claim.getDeniedDate())
                .denialReason(claim.getDenialReason())
                .patientResponsibility(claim.getPatientResponsibility())
                .insurancePayment(claim.getInsurancePayment())
                .adjustmentAmount(claim.getAdjustmentAmount())
                .adjustmentReason(claim.getAdjustmentReason())
                .notes(claim.getNotes())
                .billedByUserId(claim.getBilledByUserId())
                .billedByName(billedBy != null ? billedBy.getFullName() : null)
                .billedAt(claim.getBilledAt())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .isDeleted(claim.getIsDeleted())
                .outstandingBalance(claim.getOutstandingBalance())
                .isPaid(claim.isPaid())
                .isDenied(claim.isDenied())
                .canBeEdited(claim.canBeEdited())
                .build();
    }
}

