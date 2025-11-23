package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.*;
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
public class PlanOfCareService {

    private final PlanOfCareRepository pocRepository;
    private final PatientRepository patientRepository;
    private final EpisodeRepository episodeRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OasisAssessmentCompleteRepository oasisRepository;
    private final TaskSchedulerService taskSchedulerService;
    
    /**
     * Generate Plan of Care from OASIS Assessment
     */
    @Transactional
    public PlanOfCareDTO generateFromOASIS(Long oasisId) {
        log.info("Generating Plan of Care from OASIS assessment: {}", oasisId);
        
        OasisAssessmentComplete oasis = oasisRepository.findById(oasisId)
            .orElseThrow(() -> new RuntimeException("OASIS assessment not found"));
            
        if (!"APPROVED".equals(oasis.getStatus())) {
            throw new RuntimeException("OASIS assessment must be approved before generating POC");
        }
        
        // Check if POC already exists for this OASIS
        if (pocRepository.findByOasisAssessmentId(oasisId).isPresent()) {
            throw new RuntimeException("Plan of Care already exists for this OASIS assessment");
        }
        
        UserPrincipal currentUser = getCurrentUser();
        User clinician = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create POC
        PlanOfCare poc = PlanOfCare.builder()
            .patient(oasis.getPatient())
            .episode(oasis.getEpisode())
            .oasisAssessment(oasis)
            .organization(oasis.getOrganization())
            .pocNumber(generatePOCNumber(oasis.getOrganization()))
            .startDate(oasis.getAssessmentDate() != null ? oasis.getAssessmentDate() : LocalDate.now())
            .certificationPeriodDays(60) // Standard 60-day period
            .status("DRAFT")
            .createdByClinician(clinician)
            .physicianSignatureRequired(true)
            .physicianSignatureObtained(false)
            .build();
            
        // Calculate end date
        if (poc.getStartDate() != null && poc.getCertificationPeriodDays() != null) {
            poc.setEndDate(poc.getStartDate().plusDays(poc.getCertificationPeriodDays()));
        }
        
        // Extract diagnosis from OASIS
        extractDiagnosisFromOASIS(poc, oasis);
        
        // Extract functional limitations
        extractFunctionalInfoFromOASIS(poc, oasis);
        
        // Save POC first to get ID
        poc = pocRepository.save(poc);
        
        // Generate frequencies based on OASIS findings
        generateFrequencies(poc, oasis);
        
        // Generate interventions based on diagnosis and functional status
        generateInterventions(poc, oasis);
        
        // Generate goals based on functional deficits
        generateGoals(poc, oasis);
        
        // Generate physician orders
        generatePhysicianOrders(poc, oasis);
        
        poc = pocRepository.save(poc);
        
        log.info("Successfully generated Plan of Care: {}", poc.getPocNumber());
        return convertToDTO(poc);
    }
    
    /**
     * Create Plan of Care manually
     */
    @Transactional
    public PlanOfCareDTO createPOC(PlanOfCareRequest request, Long organizationId) {
        log.info("Creating Plan of Care for patient: {}", request.getPatientId());
        
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));
            
        Episode episode = episodeRepository.findById(request.getEpisodeId())
            .orElseThrow(() -> new RuntimeException("Episode not found"));
            
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new RuntimeException("Organization not found"));
            
        UserPrincipal currentUser = getCurrentUser();
        User clinician = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        PlanOfCare poc = PlanOfCare.builder()
            .patient(patient)
            .episode(episode)
            .organization(organization)
            .pocNumber(generatePOCNumber(organization))
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .certificationPeriodDays(request.getCertificationPeriodDays())
            .primaryDiagnosisCode(request.getPrimaryDiagnosisCode())
            .primaryDiagnosisDescription(request.getPrimaryDiagnosisDescription())
            .secondaryDiagnosisCode(request.getSecondaryDiagnosisCode())
            .secondaryDiagnosisDescription(request.getSecondaryDiagnosisDescription())
            .otherDiagnoses(request.getOtherDiagnoses())
            .functionalLimitations(request.getFunctionalLimitations())
            .safetyMeasures(request.getSafetyMeasures())
            .nutritionalRequirements(request.getNutritionalRequirements())
            .medicationList(request.getMedicationList())
            .medicationManagementNeeded(request.getMedicationManagementNeeded())
            .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
            .statusReason(request.getStatusReason())
            .physicianName(request.getPhysicianName())
            .physicianPhone(request.getPhysicianPhone())
            .physicianSignatureRequired(request.getPhysicianSignatureRequired())
            .physicianSignedDate(request.getPhysicianSignedDate())
            .physicianSignatureObtained(request.getPhysicianSignatureObtained())
            .specialInstructions(request.getSpecialInstructions())
            .dmeEquipment(request.getDmeEquipment())
            .notes(request.getNotes())
            .createdByClinician(clinician)
            .build();
            
        if (request.getOasisAssessmentId() != null) {
            OasisAssessmentComplete oasis = oasisRepository.findById(request.getOasisAssessmentId())
                .orElse(null);
            poc.setOasisAssessment(oasis);
        }
        
        poc = pocRepository.save(poc);
        
        // Add child entities if provided
        if (request.getFrequencies() != null) {
            for (PlanOfCareFrequencyRequest freqReq : request.getFrequencies()) {
                addFrequencyToPOC(poc, freqReq);
            }
        }
        
        if (request.getInterventions() != null) {
            for (PlanOfCareInterventionRequest intReq : request.getInterventions()) {
                addInterventionToPOC(poc, intReq);
            }
        }
        
        if (request.getGoals() != null) {
            for (PlanOfCareGoalRequest goalReq : request.getGoals()) {
                addGoalToPOC(poc, goalReq);
            }
        }
        
        if (request.getPhysicianOrders() != null) {
            for (PhysicianOrderRequest orderReq : request.getPhysicianOrders()) {
                addPhysicianOrderToPOC(poc, orderReq);
            }
        }
        
        poc = pocRepository.save(poc);
        
        log.info("Successfully created Plan of Care: {}", poc.getPocNumber());
        return convertToDTO(poc);
    }
    
    /**
     * Update Plan of Care
     */
    @Transactional
    public PlanOfCareDTO updatePOC(Long id, PlanOfCareRequest request) {
        log.info("Updating Plan of Care: {}", id);
        
        PlanOfCare poc = pocRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan of Care not found"));
            
        if ("APPROVED".equals(poc.getStatus()) || "ACTIVE".equals(poc.getStatus())) {
            throw new RuntimeException("Cannot update approved or active Plan of Care");
        }
        
        // Update fields
        poc.setStartDate(request.getStartDate());
        poc.setEndDate(request.getEndDate());
        poc.setCertificationPeriodDays(request.getCertificationPeriodDays());
        poc.setPrimaryDiagnosisCode(request.getPrimaryDiagnosisCode());
        poc.setPrimaryDiagnosisDescription(request.getPrimaryDiagnosisDescription());
        poc.setSecondaryDiagnosisCode(request.getSecondaryDiagnosisCode());
        poc.setSecondaryDiagnosisDescription(request.getSecondaryDiagnosisDescription());
        poc.setOtherDiagnoses(request.getOtherDiagnoses());
        poc.setFunctionalLimitations(request.getFunctionalLimitations());
        poc.setSafetyMeasures(request.getSafetyMeasures());
        poc.setNutritionalRequirements(request.getNutritionalRequirements());
        poc.setMedicationList(request.getMedicationList());
        poc.setMedicationManagementNeeded(request.getMedicationManagementNeeded());
        poc.setPhysicianName(request.getPhysicianName());
        poc.setPhysicianPhone(request.getPhysicianPhone());
        poc.setSpecialInstructions(request.getSpecialInstructions());
        poc.setDmeEquipment(request.getDmeEquipment());
        poc.setNotes(request.getNotes());
        
        poc = pocRepository.save(poc);
        
        return convertToDTO(poc);
    }
    
    /**
     * Submit POC for approval
     */
    @Transactional
    public PlanOfCareDTO submitForApproval(Long id) {
        log.info("Submitting Plan of Care for approval: {}", id);
        
        PlanOfCare poc = pocRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan of Care not found"));
            
        if (!"DRAFT".equals(poc.getStatus())) {
            throw new RuntimeException("Only draft POCs can be submitted for approval");
        }
        
        // Validate POC has required elements
        validatePOCForSubmission(poc);
        
        poc.setStatus("PENDING_APPROVAL");
        poc = pocRepository.save(poc);
        
        return convertToDTO(poc);
    }
    
    /**
     * Approve POC
     */
    @Transactional
    public PlanOfCareDTO approvePOC(Long id) {
        log.info("Approving Plan of Care: {}", id);
        
        PlanOfCare poc = pocRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan of Care not found"));
            
        if (!"PENDING_APPROVAL".equals(poc.getStatus())) {
            throw new RuntimeException("Only POCs pending approval can be approved");
        }
        
        UserPrincipal currentUser = getCurrentUser();
        User approver = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        poc.setStatus("APPROVED");
        poc.setApprovedBy(approver);
        poc.setApprovedAt(LocalDateTime.now());
        
        poc = pocRepository.save(poc);
        
        log.info("Plan of Care approved: {}", poc.getPocNumber());
        
        // Auto-generate tasks from POC after approval
        try {
            autoGenerateTasksFromPOC(poc);
        } catch (Exception e) {
            log.error("Failed to auto-generate tasks from POC {}: {}", poc.getId(), e.getMessage());
            // Don't fail the approval if task generation fails - tasks can be generated manually later
        }
        
        return convertToDTO(poc);
    }
    
    /**
     * Auto-generate tasks from approved POC
     */
    private void autoGenerateTasksFromPOC(PlanOfCare poc) {
        log.info("Auto-generating tasks from approved POC: {}", poc.getPocNumber());
        
        TaskGenerationRequest taskRequest = TaskGenerationRequest.builder()
                .planOfCareId(poc.getId())
                .startDate(poc.getStartDate())
                .assignToClinicianId(poc.getPatient().getAdmittingClinician() != null ? 
                        poc.getPatient().getAdmittingClinician().getId() : null)
                .build();
        
        taskSchedulerService.generateTasksFromPOC(taskRequest);
        log.info("Successfully auto-generated tasks from approved POC: {}", poc.getPocNumber());
    }
    
    /**
     * Reject POC
     */
    @Transactional
    public PlanOfCareDTO rejectPOC(Long id, String reason) {
        log.info("Rejecting Plan of Care: {}", id);
        
        PlanOfCare poc = pocRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan of Care not found"));
            
        UserPrincipal currentUser = getCurrentUser();
        User rejector = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        poc.setStatus("REJECTED");
        poc.setRejectedBy(rejector);
        poc.setRejectedAt(LocalDateTime.now());
        poc.setRejectionReason(reason);
        
        poc = pocRepository.save(poc);
        
        return convertToDTO(poc);
    }
    
    /**
     * Get POC by ID
     */
    @Transactional(readOnly = true)
    public PlanOfCareDTO getPOCById(Long id) {
        PlanOfCare poc = pocRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan of Care not found"));
        return convertToDTO(poc);
    }
    
    /**
     * Get all POCs for organization
     */
    @Transactional(readOnly = true)
    public List<PlanOfCareDTO> getAllPOCs(Long organizationId) {
        return pocRepository.findByOrganizationId(organizationId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get POCs for patient
     */
    @Transactional(readOnly = true)
    public List<PlanOfCareDTO> getPOCsByPatient(Long patientId) {
        return pocRepository.findByPatientId(patientId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get POCs by status
     */
    @Transactional(readOnly = true)
    public List<PlanOfCareDTO> getPOCsByStatus(Long organizationId, String status) {
        return pocRepository.findByOrganizationIdAndStatus(organizationId, status).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get POCs for clinician (role-based filtering)
     */
    @Transactional(readOnly = true)
    public List<PlanOfCareDTO> getPOCsForClinician(Long organizationId, Long clinicianId) {
        return pocRepository.findByOrganizationAndClinician(organizationId, clinicianId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Delete POC (soft delete)
     */
    @Transactional
    public void deletePOC(Long id) {
        PlanOfCare poc = pocRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan of Care not found"));
            
        if ("APPROVED".equals(poc.getStatus()) || "ACTIVE".equals(poc.getStatus())) {
            throw new RuntimeException("Cannot delete approved or active Plan of Care");
        }
        
        poc.setIsDeleted(true);
        poc.setIsActive(false);
        pocRepository.save(poc);
    }
    
    // ==================== HELPER METHODS ====================
    
    private void extractDiagnosisFromOASIS(PlanOfCare poc, OasisAssessmentComplete oasis) {
        // Extract primary diagnosis from OASIS Section I (M1021)
        if (oasis.getM1021PrimaryDiagnosisIcd() != null) {
            poc.setPrimaryDiagnosisCode(oasis.getM1021PrimaryDiagnosisIcd());
            poc.setPrimaryDiagnosisDescription(oasis.getM1021PrimaryDiagnosisDesc());
        }
        
        // Extract secondary diagnosis (M1023)
        if (oasis.getM1023OtherDiagnosis1Icd() != null) {
            poc.setSecondaryDiagnosisCode(oasis.getM1023OtherDiagnosis1Icd());
            // Note: Other diagnosis descriptions are not stored separately in OASIS entity
            // Only ICD codes and severity are stored
            poc.setSecondaryDiagnosisDescription("See OASIS Section I for details");
        }
    }
    
    private void extractFunctionalInfoFromOASIS(PlanOfCare poc, OasisAssessmentComplete oasis) {
        StringBuilder functionalLimitations = new StringBuilder();
        
        // Check ambulation status (GG0170I - Walk 10 feet)
        if (oasis.getGg0170iWalk10Admission() != null && Integer.parseInt(oasis.getGg0170iWalk10Admission()) >= 3) {
            functionalLimitations.append("Impaired ambulation. ");
        }
        
        // Check ADL deficits - Note: Bathing field may not exist in current entity
        // Using GG0130E (Shower/Bathe Self) if available, or skip if not
        // Functional limitations can be added from other GG sections
        
        poc.setFunctionalLimitations(functionalLimitations.toString());
    }
    
    private void generateFrequencies(PlanOfCare poc, OasisAssessmentComplete oasis) {
        // RN visits - Always needed for SOC
        PlanOfCareFrequency rnFreq = PlanOfCareFrequency.builder()
            .planOfCare(poc)
            .disciplineType("RN")
            .disciplineDescription("Registered Nurse")
            .frequencyCode("3W8")
            .visitsPerWeek(3)
            .numberOfWeeks(8)
            .estimatedMinutesPerVisit(60)
            .startDate(poc.getStartDate())
            .endDate(poc.getEndDate())
            .isActive(true)
            .status("PLANNED")
            .build();
        rnFreq.calculateTotalVisits();
        poc.addFrequency(rnFreq);
        
        // PT visits if mobility issues indicated
        if (needsPhysicalTherapy(oasis)) {
            PlanOfCareFrequency ptFreq = PlanOfCareFrequency.builder()
                .planOfCare(poc)
                .disciplineType("PT")
                .disciplineDescription("Physical Therapist")
                .frequencyCode("3W4")
                .visitsPerWeek(3)
                .numberOfWeeks(4)
                .estimatedMinutesPerVisit(60)
                .startDate(poc.getStartDate())
                .isActive(true)
                .status("PLANNED")
                .build();
            ptFreq.calculateTotalVisits();
            poc.addFrequency(ptFreq);
        }
        
        // HHA if ADL deficits
        if (needsHomeHealthAide(oasis)) {
            PlanOfCareFrequency hhaFreq = PlanOfCareFrequency.builder()
                .planOfCare(poc)
                .disciplineType("HHA")
                .disciplineDescription("Home Health Aide")
                .frequencyCode("5W8")
                .visitsPerWeek(5)
                .numberOfWeeks(8)
                .estimatedMinutesPerVisit(45)
                .startDate(poc.getStartDate())
                .endDate(poc.getEndDate())
                .isActive(true)
                .status("PLANNED")
                .build();
            hhaFreq.calculateTotalVisits();
            poc.addFrequency(hhaFreq);
        }
    }
    
    private void generateInterventions(PlanOfCare poc, OasisAssessmentComplete oasis) {
        // Medication management - always needed
        PlanOfCareIntervention medMgmt = PlanOfCareIntervention.builder()
            .planOfCare(poc)
            .interventionCategory("Medication Management")
            .interventionDescription("Assess medication compliance, teach about medications, monitor for side effects")
            .frequency("Each visit")
            .duration("Duration of care")
            .responsibleDiscipline("RN")
            .priority("HIGH")
            .status("ACTIVE")
            .isActive(true)
            .build();
        poc.addIntervention(medMgmt);
        
        // Vital signs monitoring
        PlanOfCareIntervention vitals = PlanOfCareIntervention.builder()
            .planOfCare(poc)
            .interventionCategory("Vital Signs Monitoring")
            .interventionDescription("Monitor vital signs and report abnormal findings to physician")
            .frequency("Each visit")
            .duration("Duration of care")
            .responsibleDiscipline("RN")
            .priority("HIGH")
            .status("ACTIVE")
            .isActive(true)
            .build();
        poc.addIntervention(vitals);
    }
    
    private void generateGoals(PlanOfCare poc, OasisAssessmentComplete oasis) {
        // Safety goal
        PlanOfCareGoal safetyGoal = PlanOfCareGoal.builder()
            .planOfCare(poc)
            .goalCategory("Safety")
            .goalType("SHORT_TERM")
            .goalDescription("Patient/caregiver will demonstrate safe medication administration")
            .measurableOutcome("Patient/caregiver verbalizes correct medication schedule and demonstrates proper technique")
            .targetDate(poc.getStartDate().plusWeeks(2))
            .status("NOT_STARTED")
            .responsibleDiscipline("RN")
            .priority("HIGH")
            .isActive(true)
            .build();
        poc.addGoal(safetyGoal);
        
        // Functional goal if mobility issues
        if (needsPhysicalTherapy(oasis)) {
            PlanOfCareGoal mobilityGoal = PlanOfCareGoal.builder()
                .planOfCare(poc)
                .goalCategory("Mobility")
                .goalType("LONG_TERM")
                .goalDescription("Patient will ambulate 50 feet with walker independently")
                .targetDate(poc.getEndDate())
                .status("NOT_STARTED")
                .responsibleDiscipline("PT")
                .priority("HIGH")
                .isActive(true)
                .build();
            poc.addGoal(mobilityGoal);
        }
    }
    
    private void generatePhysicianOrders(PlanOfCare poc, OasisAssessmentComplete oasis) {
        // Skilled nursing order
        PhysicianOrder rnOrder = PhysicianOrder.builder()
            .planOfCare(poc)
            .orderType("SKILLED_NURSING")
            .orderDescription("Skilled nursing for assessment, medication management, and patient/caregiver teaching")
            .discipline("RN")
            .frequency("3 visits per week for 8 weeks")
            .startDate(poc.getStartDate())
            .endDate(poc.getEndDate())
            .orderDate(LocalDate.now())
            .signatureRequired(true)
            .signatureObtained(false)
            .status("PENDING")
            .isActive(true)
            .build();
        poc.addPhysicianOrder(rnOrder);
    }
    
    private boolean needsPhysicalTherapy(OasisAssessmentComplete oasis) {
        // Check if patient has mobility limitations (GG0170I - Walk 10 feet)
        if (oasis.getGg0170iWalk10Admission() != null) {
            try {
                int score = Integer.parseInt(oasis.getGg0170iWalk10Admission());
                return score >= 3; // Score 3+ indicates need for assistance
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
    
    private boolean needsHomeHealthAide(OasisAssessmentComplete oasis) {
        // Check if patient has ADL limitations
        // Using GG0130E (Shower/Bathe Self) as indicator for HHA need
        if (oasis.getGg0130eShowerAdmission() != null) {
            try {
                int score = Integer.parseInt(oasis.getGg0130eShowerAdmission());
                return score >= 2; // Score 2+ indicates need for assistance
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
    
    private void validatePOCForSubmission(PlanOfCare poc) {
        if (poc.getPrimaryDiagnosisCode() == null) {
            throw new RuntimeException("Primary diagnosis is required");
        }
        if (poc.getFrequencies().isEmpty()) {
            throw new RuntimeException("At least one discipline frequency is required");
        }
    }
    
    private void addFrequencyToPOC(PlanOfCare poc, PlanOfCareFrequencyRequest request) {
        PlanOfCareFrequency freq = PlanOfCareFrequency.builder()
            .planOfCare(poc)
            .disciplineType(request.getDisciplineType())
            .disciplineDescription(request.getDisciplineDescription())
            .frequencyCode(request.getFrequencyCode())
            .visitsPerWeek(request.getVisitsPerWeek())
            .numberOfWeeks(request.getNumberOfWeeks())
            .totalVisits(request.getTotalVisits())
            .estimatedMinutesPerVisit(request.getEstimatedMinutesPerVisit())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .isActive(request.getIsActive())
            .status(request.getStatus())
            .notes(request.getNotes())
            .build();
        freq.calculateTotalVisits();
        poc.addFrequency(freq);
    }
    
    private void addInterventionToPOC(PlanOfCare poc, PlanOfCareInterventionRequest request) {
        PlanOfCareIntervention intervention = PlanOfCareIntervention.builder()
            .planOfCare(poc)
            .interventionCategory(request.getInterventionCategory())
            .interventionCode(request.getInterventionCode())
            .interventionDescription(request.getInterventionDescription())
            .frequency(request.getFrequency())
            .duration(request.getDuration())
            .responsibleDiscipline(request.getResponsibleDiscipline())
            .collaborativeDisciplines(request.getCollaborativeDisciplines())
            .priority(request.getPriority())
            .status(request.getStatus())
            .isActive(request.getIsActive())
            .expectedOutcome(request.getExpectedOutcome())
            .specialInstructions(request.getSpecialInstructions())
            .contraindications(request.getContraindications())
            .notes(request.getNotes())
            .build();
        poc.addIntervention(intervention);
    }
    
    private void addGoalToPOC(PlanOfCare poc, PlanOfCareGoalRequest request) {
        PlanOfCareGoal goal = PlanOfCareGoal.builder()
            .planOfCare(poc)
            .goalCategory(request.getGoalCategory())
            .goalType(request.getGoalType())
            .goalDescription(request.getGoalDescription())
            .measurableOutcome(request.getMeasurableOutcome())
            .baselineMeasure(request.getBaselineMeasure())
            .targetMeasure(request.getTargetMeasure())
            .targetDate(request.getTargetDate())
            .reviewDate(request.getReviewDate())
            .status(request.getStatus())
            .progressPercentage(request.getProgressPercentage())
            .progressNotes(request.getProgressNotes())
            .responsibleDiscipline(request.getResponsibleDiscipline())
            .priority(request.getPriority())
            .isActive(request.getIsActive())
            .barriers(request.getBarriers())
            .interventionsRelated(request.getInterventionsRelated())
            .notes(request.getNotes())
            .build();
        poc.addGoal(goal);
    }
    
    private void addPhysicianOrderToPOC(PlanOfCare poc, PhysicianOrderRequest request) {
        PhysicianOrder order = PhysicianOrder.builder()
            .planOfCare(poc)
            .orderNumber(request.getOrderNumber())
            .orderType(request.getOrderType())
            .orderDescription(request.getOrderDescription())
            .discipline(request.getDiscipline())
            .frequency(request.getFrequency())
            .duration(request.getDuration())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .orderingPhysicianName(request.getOrderingPhysicianName())
            .orderingPhysicianNpi(request.getOrderingPhysicianNpi())
            .orderingPhysicianPhone(request.getOrderingPhysicianPhone())
            .orderDate(request.getOrderDate())
            .signatureRequired(request.getSignatureRequired())
            .signatureObtained(request.getSignatureObtained())
            .signatureDate(request.getSignatureDate())
            .verbalOrder(request.getVerbalOrder())
            .verbalOrderReceivedBy(request.getVerbalOrderReceivedBy())
            .verbalOrderDate(request.getVerbalOrderDate())
            .status(request.getStatus())
            .statusReason(request.getStatusReason())
            .isActive(request.getIsActive())
            .diagnosisCodes(request.getDiagnosisCodes())
            .clinicalJustification(request.getClinicalJustification())
            .precautions(request.getPrecautions())
            .contraindications(request.getContraindications())
            .renewable(request.getRenewable())
            .renewalDate(request.getRenewalDate())
            .specialInstructions(request.getSpecialInstructions())
            .notes(request.getNotes())
            .build();
        poc.addPhysicianOrder(order);
    }
    
    private String generatePOCNumber(Organization organization) {
        String orgName = organization.getOrganizationName() != null ? organization.getOrganizationName() : "ORG";
        String prefix = orgName.substring(0, Math.min(3, orgName.length())).toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "POC-" + prefix + "-" + timestamp.substring(timestamp.length() - 8);
    }
    
    private PlanOfCareDTO convertToDTO(PlanOfCare poc) {
        return PlanOfCareDTO.builder()
            .id(poc.getId())
            .pocNumber(poc.getPocNumber())
            .patientId(poc.getPatient().getId())
            .patientName(poc.getPatient().getFullName())
            .episodeId(poc.getEpisode().getId())
            .episodeNumber(poc.getEpisode().getEpisodeNumber())
            .oasisAssessmentId(poc.getOasisAssessment() != null ? poc.getOasisAssessment().getId() : null)
            .organizationId(poc.getOrganization().getId())
            .startDate(poc.getStartDate())
            .endDate(poc.getEndDate())
            .certificationPeriodDays(poc.getCertificationPeriodDays())
            .primaryDiagnosisCode(poc.getPrimaryDiagnosisCode())
            .primaryDiagnosisDescription(poc.getPrimaryDiagnosisDescription())
            .secondaryDiagnosisCode(poc.getSecondaryDiagnosisCode())
            .secondaryDiagnosisDescription(poc.getSecondaryDiagnosisDescription())
            .otherDiagnoses(poc.getOtherDiagnoses())
            .functionalLimitations(poc.getFunctionalLimitations())
            .safetyMeasures(poc.getSafetyMeasures())
            .nutritionalRequirements(poc.getNutritionalRequirements())
            .medicationList(poc.getMedicationList())
            .medicationManagementNeeded(poc.getMedicationManagementNeeded())
            .status(poc.getStatus())
            .statusReason(poc.getStatusReason())
            .physicianName(poc.getPhysicianName())
            .physicianPhone(poc.getPhysicianPhone())
            .physicianSignatureRequired(poc.getPhysicianSignatureRequired())
            .physicianSignedDate(poc.getPhysicianSignedDate())
            .physicianSignatureObtained(poc.getPhysicianSignatureObtained())
            .createdByClinicianId(poc.getCreatedByClinician() != null ? poc.getCreatedByClinician().getId() : null)
            .createdByClinicianName(poc.getCreatedByClinician() != null ? poc.getCreatedByClinician().getFullName() : null)
            .approvedById(poc.getApprovedBy() != null ? poc.getApprovedBy().getId() : null)
            .approvedByName(poc.getApprovedBy() != null ? poc.getApprovedBy().getFullName() : null)
            .approvedAt(poc.getApprovedAt())
            .rejectedById(poc.getRejectedBy() != null ? poc.getRejectedBy().getId() : null)
            .rejectedByName(poc.getRejectedBy() != null ? poc.getRejectedBy().getFullName() : null)
            .rejectedAt(poc.getRejectedAt())
            .rejectionReason(poc.getRejectionReason())
            .specialInstructions(poc.getSpecialInstructions())
            .dmeEquipment(poc.getDmeEquipment())
            .notes(poc.getNotes())
            .createdAt(poc.getCreatedAt())
            .updatedAt(poc.getUpdatedAt())
            .createdBy(poc.getCreatedBy())
            .updatedBy(poc.getUpdatedBy())
            .remainingDays(poc.getRemainingDays())
            .isExpiringSoon(poc.isExpiringSoon())
            .isExpired(poc.isExpired())
            .build();
    }
    
    private UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrincipal) authentication.getPrincipal();
    }
}

