package com.oasis.homehealth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oasis.homehealth.dto.OasisAssessmentDTO;
import com.oasis.homehealth.dto.OasisAssessmentRequest;
import com.oasis.homehealth.dto.OasisQARequest;
import com.oasis.homehealth.entity.*;
import com.oasis.homehealth.repository.*;
import com.oasis.homehealth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OasisAssessmentService {

    private final OasisAssessmentRepository oasisRepository;
    private final PatientRepository patientRepository;
    private final EpisodeRepository episodeRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Create a new OASIS assessment
     */
    public OasisAssessmentDTO createAssessment(OasisAssessmentRequest request) {
        log.info("Creating OASIS assessment for patient: {}", request.getPatientId());

        // Get current user
        UserPrincipal userPrincipal = getCurrentUser();
        Long organizationId = userPrincipal.getOrganizationId();

        // Validate patient exists and belongs to organization
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (!patient.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Patient does not belong to your organization");
        }

        // Validate episode if provided
        Episode episode = null;
        if (request.getEpisodeId() != null) {
            episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        }

        // Get organization
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Get clinician if provided
        User clinician = null;
        if (request.getClinicianId() != null) {
            clinician = userRepository.findById(request.getClinicianId())
                .orElse(null);
        }

        // Create assessment entity
        OasisAssessment assessment = OasisAssessment.builder()
            .patient(patient)
            .episode(episode)
            .organization(organization)
            .clinician(clinician)
            .assessmentType(request.getAssessmentType())
            .assessmentReason(request.getAssessmentReason())
            .assessmentDate(request.getAssessmentDate())
            .status("DRAFT")
            .completionPercentage(0)
            .lastAutoSaved(LocalDateTime.now())
            .build();

        // Map all fields from request
        mapRequestToEntity(request, assessment);

        // Calculate skipped fields and completion
        updateSkipLogicAndCompletion(assessment);

        // Save
        assessment = oasisRepository.save(assessment);

        log.info("OASIS assessment created with ID: {}", assessment.getId());
        return mapToDTO(assessment);
    }

    /**
     * Update existing assessment (auto-save or manual save)
     */
    public OasisAssessmentDTO updateAssessment(Long id, OasisAssessmentRequest request) {
        log.info("Updating OASIS assessment: {}", id);

        OasisAssessment assessment = oasisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // Validate user can edit
        validateCanEdit(assessment);

        // Update fields
        mapRequestToEntity(request, assessment);

        // Update auto-save timestamp
        assessment.setLastAutoSaved(LocalDateTime.now());

        // Calculate skipped fields and completion
        updateSkipLogicAndCompletion(assessment);

        // Save
        assessment = oasisRepository.save(assessment);

        log.info("OASIS assessment updated: {}", id);
        return mapToDTO(assessment);
    }

    /**
     * Auto-save assessment (called every 15 seconds from frontend)
     */
    public OasisAssessmentDTO autoSaveAssessment(Long id, OasisAssessmentRequest request) {
        log.debug("Auto-saving OASIS assessment: {}", id);
        return updateAssessment(id, request);
    }

    /**
     * Submit assessment for QA review
     */
    public OasisAssessmentDTO submitForQA(Long id) {
        log.info("Submitting OASIS assessment for QA: {}", id);

        OasisAssessment assessment = oasisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // Validate user can edit
        validateCanEdit(assessment);

        // Validate completion
        if (assessment.getCompletionPercentage() < 100) {
            throw new RuntimeException("Assessment must be 100% complete before submission");
        }

        // Update status
        UserPrincipal userPrincipal = getCurrentUser();
        User submitter = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        assessment.setStatus("SUBMITTED");
        assessment.setSubmittedBy(submitter);
        assessment.setSubmittedAt(LocalDateTime.now());

        assessment = oasisRepository.save(assessment);

        log.info("OASIS assessment submitted for QA: {}", id);
        return mapToDTO(assessment);
    }

    /**
     * QA Review - Approve or Reject
     */
    public OasisAssessmentDTO reviewAssessment(OasisQARequest request) {
        log.info("QA reviewing OASIS assessment: {}", request.getAssessmentId());

        OasisAssessment assessment = oasisRepository.findById(request.getAssessmentId())
            .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // Validate status
        if (!"SUBMITTED".equals(assessment.getStatus())) {
            throw new RuntimeException("Assessment is not in SUBMITTED status");
        }

        // Get reviewer
        UserPrincipal userPrincipal = getCurrentUser();
        User reviewer = userRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        assessment.setReviewedBy(reviewer);
        assessment.setReviewedAt(LocalDateTime.now());
        assessment.setQaComments(request.getComments());

        if ("APPROVE".equals(request.getAction())) {
            assessment.setStatus("APPROVED");
            assessment.setLockedAt(LocalDateTime.now());
            log.info("OASIS assessment APPROVED: {}", request.getAssessmentId());
        } else if ("REJECT".equals(request.getAction())) {
            assessment.setStatus("REJECTED");
            log.info("OASIS assessment REJECTED: {}", request.getAssessmentId());
        } else {
            throw new RuntimeException("Invalid action: " + request.getAction());
        }

        assessment = oasisRepository.save(assessment);
        return mapToDTO(assessment);
    }

    /**
     * Lock assessment (final lock after approval)
     */
    public OasisAssessmentDTO lockAssessment(Long id) {
        log.info("Locking OASIS assessment: {}", id);

        OasisAssessment assessment = oasisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (!"APPROVED".equals(assessment.getStatus())) {
            throw new RuntimeException("Only approved assessments can be locked");
        }

        assessment.setStatus("LOCKED");
        assessment.setLockedAt(LocalDateTime.now());

        assessment = oasisRepository.save(assessment);

        log.info("OASIS assessment locked: {}", id);
        return mapToDTO(assessment);
    }

    /**
     * Get assessment by ID
     */
    @Transactional(readOnly = true)
    public OasisAssessmentDTO getAssessment(Long id) {
        OasisAssessment assessment = oasisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // Validate organization access
        UserPrincipal userPrincipal = getCurrentUser();
        if (!assessment.getOrganization().getId().equals(userPrincipal.getOrganizationId())) {
            throw new RuntimeException("Access denied");
        }

        return mapToDTO(assessment);
    }

    /**
     * Get all assessments for a patient
     */
    @Transactional(readOnly = true)
    public List<OasisAssessmentDTO> getAssessmentsByPatient(Long patientId) {
        List<OasisAssessment> assessments = oasisRepository.findByPatientIdAndIsDeletedFalse(patientId);
        return assessments.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get all assessments for an episode
     */
    @Transactional(readOnly = true)
    public List<OasisAssessmentDTO> getAssessmentsByEpisode(Long episodeId) {
        List<OasisAssessment> assessments = oasisRepository.findByEpisodeIdAndIsDeletedFalse(episodeId);
        return assessments.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get assessments pending QA review
     */
    @Transactional(readOnly = true)
    public List<OasisAssessmentDTO> getPendingQAReviews(Long organizationId) {
        try {
            // If organizationId is null, get all pending reviews (for SYSTEM_ADMIN)
            if (organizationId == null) {
                log.info("Fetching all pending QA reviews (no organization filter)");
                List<OasisAssessment> assessments = oasisRepository.findAllPendingQAReviews();
                log.info("Found {} pending QA reviews across all organizations", assessments.size());
                return assessments.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            }
            
            log.info("Fetching pending QA reviews for organization: {}", organizationId);
            List<OasisAssessment> assessments = oasisRepository.findPendingQAReviews(organizationId);
            log.info("Found {} pending QA reviews", assessments.size());
            
            return assessments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Error in getPendingQAReviews", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getPendingQAReviews", e);
            throw new RuntimeException("Failed to retrieve pending QA reviews: " + e.getMessage(), e);
        }
    }

    /**
     * Get incomplete assessments (DRAFT)
     */
    @Transactional(readOnly = true)
    public List<OasisAssessmentDTO> getIncompleteAssessments(Long organizationId) {
        try {
            if (organizationId == null) {
                log.error("Organization ID is null");
                throw new RuntimeException("Organization ID is required. Please select an organization.");
            }
            
            List<OasisAssessment> assessments = oasisRepository.findIncompleteAssessments(organizationId);
            return assessments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Error in getIncompleteAssessments", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getIncompleteAssessments", e);
            throw new RuntimeException("Failed to retrieve incomplete assessments: " + e.getMessage(), e);
        }
    }

    /**
     * Delete assessment (soft delete)
     */
    public void deleteAssessment(Long id) {
        log.info("Deleting OASIS assessment: {}", id);

        OasisAssessment assessment = oasisRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // Validate user can delete
        validateCanEdit(assessment);

        assessment.setIsDeleted(true);
        oasisRepository.save(assessment);

        log.info("OASIS assessment deleted: {}", id);
    }

    // ============================================
    // SKIP LOGIC & COMPLETION CALCULATION
    // ============================================

    /**
     * Update skip logic and calculate completion percentage
     */
    private void updateSkipLogicAndCompletion(OasisAssessment assessment) {
        List<String> skippedFields = calculateSkippedFields(assessment);
        assessment.setSkippedFields(toJson(skippedFields));

        int completionPercentage = calculateCompletionPercentage(assessment, skippedFields);
        assessment.setCompletionPercentage(completionPercentage);
    }

    /**
     * Calculate which fields should be skipped based on skip logic rules
     */
    private List<String> calculateSkippedFields(OasisAssessment assessment) {
        List<String> skipped = new ArrayList<>();

        // M1005: Skip if M1000 = "NA"
        if ("NA".equals(assessment.getM1000InpatientFacility())) {
            skipped.add("M1005");
        }

        // M1307-M1324: Skip if M1306 = "0" (no pressure ulcers)
        if ("0".equals(assessment.getM1306PressureUlcer())) {
            skipped.addAll(Arrays.asList("M1307", "M1308", "M1320"));
        }

        // M1332-M1334: Skip if M1330 = "0" (no stasis ulcers)
        if ("0".equals(assessment.getM1330StasisUlcer())) {
            skipped.addAll(Arrays.asList("M1332", "M1334"));
        }

        // M1342: Skip if M1340 = "0" (no surgical wound)
        if ("0".equals(assessment.getM1340SurgicalWound())) {
            skipped.add("M1342");
        }

        // M1615: Skip if M1610 = "0" (continent)
        if ("0".equals(assessment.getM1610UrinaryIncontinence())) {
            skipped.add("M1615");
        }

        // M2410: Skip if M2310 = "0" (no emergent care)
        if ("0".equals(assessment.getM2310EmergentCare())) {
            skipped.add("M2410");
        }

        // PHQ-9: Skip if PHQ-2 score < 3
        if (assessment.shouldSkipPhq9()) {
            skipped.addAll(Arrays.asList("D0150_Q3", "D0150_Q4", "D0150_Q5", 
                "D0150_Q6", "D0150_Q7", "D0150_Q8", "D0150_Q9", "D0160"));
        }

        return skipped;
    }

    /**
     * Calculate completion percentage (excluding skipped fields)
     */
    private int calculateCompletionPercentage(OasisAssessment assessment, List<String> skippedFields) {
        // Define total required fields (simplified - in production, this would be more comprehensive)
        int totalFields = 100; // Approximate total OASIS fields
        int skippedCount = skippedFields.size();
        int requiredFields = totalFields - skippedCount;

        // Count filled fields (non-null, non-empty)
        int filledFields = countFilledFields(assessment, skippedFields);

        // Calculate percentage
        if (requiredFields == 0) return 100;
        return Math.min(100, (filledFields * 100) / requiredFields);
    }

    /**
     * Count how many fields are filled (excluding skipped)
     */
    private int countFilledFields(OasisAssessment assessment, List<String> skippedFields) {
        int count = 0;

        // Section 1: Administrative
        if (isNotEmpty(assessment.getM0010CmsCertNumber())) count++;
        if (isNotEmpty(assessment.getM0014BranchState())) count++;
        if (isNotEmpty(assessment.getM0016BranchId())) count++;
        if (isNotEmpty(assessment.getM0018Npi())) count++;
        if (isNotEmpty(assessment.getM0020PatientId())) count++;
        if (assessment.getM0030SocDate() != null) count++;
        if (isNotEmpty(assessment.getM0063MedicareNumber())) count++;
        if (isNotEmpty(assessment.getM0069Gender())) count++;

        // Section 2: Diagnoses
        if (isNotEmpty(assessment.getM1000InpatientFacility())) count++;
        if (!skippedFields.contains("M1005") && assessment.getM1005InpatientDischargeDate() != null) count++;
        if (isNotEmpty(assessment.getM1021PrimaryDiagnosisIcd())) count++;

        // Section 5: Integumentary
        if (isNotEmpty(assessment.getM1306PressureUlcer())) count++;
        if (!skippedFields.contains("M1307") && assessment.getM1307OldestStage2Date() != null) count++;
        if (!skippedFields.contains("M1308") && assessment.getM1308Stage2Count() != null) count++;

        // Section 8: Elimination
        if (isNotEmpty(assessment.getM1610UrinaryIncontinence())) count++;
        if (!skippedFields.contains("M1615") && isNotEmpty(assessment.getM1615IncontinenceTiming())) count++;

        // Section 15: PHQ
        if (isNotEmpty(assessment.getD0150Phq2Interest())) count++;
        if (isNotEmpty(assessment.getD0150Phq2Depressed())) count++;
        if (!skippedFields.contains("D0150_Q3") && isNotEmpty(assessment.getD0150Phq9Q3())) count++;

        // Add more field checks as needed...
        // This is a simplified version. In production, you'd iterate through all fields.

        return count;
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Map request DTO to entity
     */
    private void mapRequestToEntity(OasisAssessmentRequest request, OasisAssessment assessment) {
        // Section 1: Administrative
        assessment.setM0010CmsCertNumber(request.getM0010CmsCertNumber());
        assessment.setM0014BranchState(request.getM0014BranchState());
        assessment.setM0016BranchId(request.getM0016BranchId());
        assessment.setM0018Npi(request.getM0018Npi());
        assessment.setM0020PatientId(request.getM0020PatientId());
        assessment.setM0030SocDate(request.getM0030SocDate());
        assessment.setM0032RocDate(request.getM0032RocDate());
        assessment.setM0063MedicareNumber(request.getM0063MedicareNumber());
        assessment.setM0064Ssn(request.getM0064Ssn());
        assessment.setM0065MedicaidNumber(request.getM0065MedicaidNumber());
        assessment.setM0069Gender(request.getM0069Gender());
        assessment.setM0140RaceEthnicity(request.getM0140RaceEthnicity());

        // Section 2: Diagnoses
        assessment.setM1000InpatientFacility(request.getM1000InpatientFacility());
        assessment.setM1005InpatientDischargeDate(request.getM1005InpatientDischargeDate());
        assessment.setM1011InpatientDiagnosis(request.getM1011InpatientDiagnosis());
        assessment.setM1017DiagnosisChange(request.getM1017DiagnosisChange());
        assessment.setM1021PrimaryDiagnosisIcd(request.getM1021PrimaryDiagnosisIcd());
        assessment.setM1021PrimaryDiagnosisDesc(request.getM1021PrimaryDiagnosisDesc());
        assessment.setM1023OtherDiagnosis1Icd(request.getM1023OtherDiagnosis1Icd());
        assessment.setM1023OtherDiagnosis2Icd(request.getM1023OtherDiagnosis2Icd());
        assessment.setM1023OtherDiagnosis3Icd(request.getM1023OtherDiagnosis3Icd());
        assessment.setM1023OtherDiagnosis4Icd(request.getM1023OtherDiagnosis4Icd());
        assessment.setM1023OtherDiagnosis5Icd(request.getM1023OtherDiagnosis5Icd());
        assessment.setM1028ActiveDiagnoses(request.getM1028ActiveDiagnoses());

        // Section 3: Living
        assessment.setM1100LivingSituation(request.getM1100LivingSituation());

        // Section 4: Sensory
        assessment.setM1200Vision(request.getM1200Vision());
        assessment.setM1242Hearing(request.getM1242Hearing());

        // Section 5: Integumentary
        assessment.setM1306PressureUlcer(request.getM1306PressureUlcer());
        assessment.setM1307OldestStage2Date(request.getM1307OldestStage2Date());
        assessment.setM1308Stage1Count(request.getM1308Stage1Count());
        assessment.setM1308Stage2Count(request.getM1308Stage2Count());
        assessment.setM1308Stage3Count(request.getM1308Stage3Count());
        assessment.setM1308Stage4Count(request.getM1308Stage4Count());
        assessment.setM1308UnstageableCount(request.getM1308UnstageableCount());
        assessment.setM1320PressureUlcerStatus(request.getM1320PressureUlcerStatus());
        assessment.setM1330StasisUlcer(request.getM1330StasisUlcer());
        assessment.setM1332StasisUlcerCount(request.getM1332StasisUlcerCount());
        assessment.setM1334StasisUlcerStatus(request.getM1334StasisUlcerStatus());
        assessment.setM1340SurgicalWound(request.getM1340SurgicalWound());
        assessment.setM1342SurgicalWoundStatus(request.getM1342SurgicalWoundStatus());

        // Section 6: Respiratory
        assessment.setM1400Dyspnea(request.getM1400Dyspnea());
        assessment.setM1410RespiratoryTreatments(request.getM1410RespiratoryTreatments());

        // Section 7: Cardiac
        assessment.setM1500HeartFailureSymptoms(request.getM1500HeartFailureSymptoms());

        // Section 8: Elimination
        assessment.setM1600UtiTreatment(request.getM1600UtiTreatment());
        assessment.setM1610UrinaryIncontinence(request.getM1610UrinaryIncontinence());
        assessment.setM1615IncontinenceTiming(request.getM1615IncontinenceTiming());
        assessment.setM1620BowelIncontinence(request.getM1620BowelIncontinence());
        assessment.setM1630Ostomy(request.getM1630Ostomy());

        // Section 9: Neuro/Emotional
        assessment.setM1700CognitiveFunctioning(request.getM1700CognitiveFunctioning());
        assessment.setM1710WhenConfused(request.getM1710WhenConfused());
        assessment.setM1720WhenAnxious(request.getM1720WhenAnxious());
        assessment.setM1730DepressionScreening(request.getM1730DepressionScreening());
        assessment.setM1740PsychiatricSymptoms(request.getM1740PsychiatricSymptoms());
        assessment.setM1745DisruptiveBehaviorFreq(request.getM1745DisruptiveBehaviorFreq());

        // Section 10: Functional (JSON fields)
        assessment.setGg0100PriorFunctioning(toJson(request.getGg0100PriorFunctioning()));
        assessment.setGg0110PriorDeviceUse(toJson(request.getGg0110PriorDeviceUse()));
        assessment.setGg0130SelfCare(toJson(request.getGg0130SelfCare()));
        assessment.setGg0170Mobility(toJson(request.getGg0170Mobility()));

        // Section 11: Medications
        assessment.setM2001DrugRegimenReview(request.getM2001DrugRegimenReview());
        assessment.setM2003MedicationFollowup(request.getM2003MedicationFollowup());
        assessment.setM2005MedicationIntervention(request.getM2005MedicationIntervention());
        assessment.setM2010HighRiskDrugEducation(request.getM2010HighRiskDrugEducation());
        assessment.setM2015DrugEducationIntervention(request.getM2015DrugEducationIntervention());
        assessment.setM2020OralMedicationManagement(request.getM2020OralMedicationManagement());
        assessment.setM2030InjectableMedicationMgmt(request.getM2030InjectableMedicationMgmt());
        assessment.setM2040PriorMedicationMgmt(request.getM2040PriorMedicationMgmt());

        // Section 12: Care Management
        assessment.setM2102AssistanceTypes(toJson(request.getM2102AssistanceTypes()));
        assessment.setM2110AssistanceFrequency(request.getM2110AssistanceFrequency());

        // Section 13: Emergent Care
        assessment.setM2310EmergentCare(request.getM2310EmergentCare());
        assessment.setM2410EmergentCareReason(request.getM2410EmergentCareReason());

        // Section 14: COVID
        assessment.setO0350CovidVaccination(request.getO0350CovidVaccination());

        // Section 15: PHQ
        assessment.setD0150Phq2Interest(request.getD0150Phq2Interest());
        assessment.setD0150Phq2Depressed(request.getD0150Phq2Depressed());
        assessment.setD0150Phq9Q3(request.getD0150Phq9Q3());
        assessment.setD0150Phq9Q4(request.getD0150Phq9Q4());
        assessment.setD0150Phq9Q5(request.getD0150Phq9Q5());
        assessment.setD0150Phq9Q6(request.getD0150Phq9Q6());
        assessment.setD0150Phq9Q7(request.getD0150Phq9Q7());
        assessment.setD0150Phq9Q8(request.getD0150Phq9Q8());
        assessment.setD0150Phq9Q9(request.getD0150Phq9Q9());
        assessment.setD0160Phq9TotalScore(request.getD0160Phq9TotalScore());

        // Section 16: Immunization
        assessment.setM1041InfluenzaVaccinePeriod(request.getM1041InfluenzaVaccinePeriod());
        assessment.setM1046InfluenzaVaccineReceived(request.getM1046InfluenzaVaccineReceived());
        assessment.setM1051PneumococcalVaccine(request.getM1051PneumococcalVaccine());

        // Section 17: Discharge
        assessment.setM2401InterventionSynopsis(toJson(request.getM2401InterventionSynopsis()));
        assessment.setM2410DischargeTo(request.getM2410DischargeTo());
        assessment.setM2420DischargeDisposition(request.getM2420DischargeDisposition());

        // Section completion
        assessment.setSectionCompletion(toJson(request.getSectionCompletion()));
    }

    /**
     * Map entity to DTO
     */
    private OasisAssessmentDTO mapToDTO(OasisAssessment assessment) {
        OasisAssessmentDTO dto = OasisAssessmentDTO.builder()
            .id(assessment.getId())
            .patientId(assessment.getPatient().getId())
            .patientName(assessment.getPatient().getFirstName() + " " + assessment.getPatient().getLastName())
            .episodeId(assessment.getEpisode() != null ? assessment.getEpisode().getId() : null)
            .organizationId(assessment.getOrganization().getId())
            .clinicianId(assessment.getClinician() != null ? assessment.getClinician().getId() : null)
            .clinicianName(assessment.getClinician() != null ? assessment.getClinician().getFullName() : null)
            .assessmentType(assessment.getAssessmentType())
            .assessmentReason(assessment.getAssessmentReason())
            .assessmentDate(assessment.getAssessmentDate())
            .status(assessment.getStatus())
            .completionPercentage(assessment.getCompletionPercentage())
            .lastAutoSaved(assessment.getLastAutoSaved())
            .submittedById(assessment.getSubmittedBy() != null ? assessment.getSubmittedBy().getId() : null)
            .submittedByName(assessment.getSubmittedBy() != null ? assessment.getSubmittedBy().getFullName() : null)
            .submittedAt(assessment.getSubmittedAt())
            .reviewedById(assessment.getReviewedBy() != null ? assessment.getReviewedBy().getId() : null)
            .reviewedByName(assessment.getReviewedBy() != null ? assessment.getReviewedBy().getFullName() : null)
            .reviewedAt(assessment.getReviewedAt())
            .qaComments(assessment.getQaComments())
            .lockedAt(assessment.getLockedAt())
            .sectionCompletion(fromJson(assessment.getSectionCompletion(), new TypeReference<Map<String, Boolean>>() {}))
            .skippedFields(fromJson(assessment.getSkippedFields(), new TypeReference<List<String>>() {}))
            .build();

        // Map all OASIS fields (abbreviated for brevity - full mapping would include all 100+ fields)
        // Section 1
        dto.setM0010CmsCertNumber(assessment.getM0010CmsCertNumber());
        dto.setM0014BranchState(assessment.getM0014BranchState());
        dto.setM0016BranchId(assessment.getM0016BranchId());
        dto.setM0018Npi(assessment.getM0018Npi());
        dto.setM0020PatientId(assessment.getM0020PatientId());
        dto.setM0030SocDate(assessment.getM0030SocDate());
        dto.setM0032RocDate(assessment.getM0032RocDate());
        dto.setM0063MedicareNumber(assessment.getM0063MedicareNumber());
        dto.setM0064Ssn(assessment.getM0064Ssn());
        dto.setM0065MedicaidNumber(assessment.getM0065MedicaidNumber());
        dto.setM0069Gender(assessment.getM0069Gender());
        dto.setM0140RaceEthnicity(assessment.getM0140RaceEthnicity());

        // Section 2
        dto.setM1000InpatientFacility(assessment.getM1000InpatientFacility());
        dto.setM1005InpatientDischargeDate(assessment.getM1005InpatientDischargeDate());
        dto.setM1011InpatientDiagnosis(assessment.getM1011InpatientDiagnosis());
        dto.setM1017DiagnosisChange(assessment.getM1017DiagnosisChange());
        dto.setM1021PrimaryDiagnosisIcd(assessment.getM1021PrimaryDiagnosisIcd());
        dto.setM1021PrimaryDiagnosisDesc(assessment.getM1021PrimaryDiagnosisDesc());
        dto.setM1023OtherDiagnosis1Icd(assessment.getM1023OtherDiagnosis1Icd());
        dto.setM1023OtherDiagnosis2Icd(assessment.getM1023OtherDiagnosis2Icd());
        dto.setM1023OtherDiagnosis3Icd(assessment.getM1023OtherDiagnosis3Icd());
        dto.setM1023OtherDiagnosis4Icd(assessment.getM1023OtherDiagnosis4Icd());
        dto.setM1023OtherDiagnosis5Icd(assessment.getM1023OtherDiagnosis5Icd());
        dto.setM1028ActiveDiagnoses(assessment.getM1028ActiveDiagnoses());

        // Section 3
        dto.setM1100LivingSituation(assessment.getM1100LivingSituation());

        // Section 4
        dto.setM1200Vision(assessment.getM1200Vision());
        dto.setM1242Hearing(assessment.getM1242Hearing());

        // Section 5
        dto.setM1306PressureUlcer(assessment.getM1306PressureUlcer());
        dto.setM1307OldestStage2Date(assessment.getM1307OldestStage2Date());
        dto.setM1308Stage1Count(assessment.getM1308Stage1Count());
        dto.setM1308Stage2Count(assessment.getM1308Stage2Count());
        dto.setM1308Stage3Count(assessment.getM1308Stage3Count());
        dto.setM1308Stage4Count(assessment.getM1308Stage4Count());
        dto.setM1308UnstageableCount(assessment.getM1308UnstageableCount());
        dto.setM1320PressureUlcerStatus(assessment.getM1320PressureUlcerStatus());
        dto.setM1330StasisUlcer(assessment.getM1330StasisUlcer());
        dto.setM1332StasisUlcerCount(assessment.getM1332StasisUlcerCount());
        dto.setM1334StasisUlcerStatus(assessment.getM1334StasisUlcerStatus());
        dto.setM1340SurgicalWound(assessment.getM1340SurgicalWound());
        dto.setM1342SurgicalWoundStatus(assessment.getM1342SurgicalWoundStatus());

        // Section 6
        dto.setM1400Dyspnea(assessment.getM1400Dyspnea());
        dto.setM1410RespiratoryTreatments(assessment.getM1410RespiratoryTreatments());

        // Section 7
        dto.setM1500HeartFailureSymptoms(assessment.getM1500HeartFailureSymptoms());

        // Section 8
        dto.setM1600UtiTreatment(assessment.getM1600UtiTreatment());
        dto.setM1610UrinaryIncontinence(assessment.getM1610UrinaryIncontinence());
        dto.setM1615IncontinenceTiming(assessment.getM1615IncontinenceTiming());
        dto.setM1620BowelIncontinence(assessment.getM1620BowelIncontinence());
        dto.setM1630Ostomy(assessment.getM1630Ostomy());

        // Section 9
        dto.setM1700CognitiveFunctioning(assessment.getM1700CognitiveFunctioning());
        dto.setM1710WhenConfused(assessment.getM1710WhenConfused());
        dto.setM1720WhenAnxious(assessment.getM1720WhenAnxious());
        dto.setM1730DepressionScreening(assessment.getM1730DepressionScreening());
        dto.setM1740PsychiatricSymptoms(assessment.getM1740PsychiatricSymptoms());
        dto.setM1745DisruptiveBehaviorFreq(assessment.getM1745DisruptiveBehaviorFreq());

        // Section 10 (JSON fields)
        dto.setGg0100PriorFunctioning(fromJson(assessment.getGg0100PriorFunctioning(), new TypeReference<Map<String, Object>>() {}));
        dto.setGg0110PriorDeviceUse(fromJson(assessment.getGg0110PriorDeviceUse(), new TypeReference<Map<String, Object>>() {}));
        dto.setGg0130SelfCare(fromJson(assessment.getGg0130SelfCare(), new TypeReference<Map<String, Object>>() {}));
        dto.setGg0170Mobility(fromJson(assessment.getGg0170Mobility(), new TypeReference<Map<String, Object>>() {}));

        // Section 11
        dto.setM2001DrugRegimenReview(assessment.getM2001DrugRegimenReview());
        dto.setM2003MedicationFollowup(assessment.getM2003MedicationFollowup());
        dto.setM2005MedicationIntervention(assessment.getM2005MedicationIntervention());
        dto.setM2010HighRiskDrugEducation(assessment.getM2010HighRiskDrugEducation());
        dto.setM2015DrugEducationIntervention(assessment.getM2015DrugEducationIntervention());
        dto.setM2020OralMedicationManagement(assessment.getM2020OralMedicationManagement());
        dto.setM2030InjectableMedicationMgmt(assessment.getM2030InjectableMedicationMgmt());
        dto.setM2040PriorMedicationMgmt(assessment.getM2040PriorMedicationMgmt());

        // Section 12
        dto.setM2102AssistanceTypes(fromJson(assessment.getM2102AssistanceTypes(), new TypeReference<Map<String, Object>>() {}));
        dto.setM2110AssistanceFrequency(assessment.getM2110AssistanceFrequency());

        // Section 13
        dto.setM2310EmergentCare(assessment.getM2310EmergentCare());
        dto.setM2410EmergentCareReason(assessment.getM2410EmergentCareReason());

        // Section 14
        dto.setO0350CovidVaccination(assessment.getO0350CovidVaccination());

        // Section 15
        dto.setD0150Phq2Interest(assessment.getD0150Phq2Interest());
        dto.setD0150Phq2Depressed(assessment.getD0150Phq2Depressed());
        dto.setD0150Phq9Q3(assessment.getD0150Phq9Q3());
        dto.setD0150Phq9Q4(assessment.getD0150Phq9Q4());
        dto.setD0150Phq9Q5(assessment.getD0150Phq9Q5());
        dto.setD0150Phq9Q6(assessment.getD0150Phq9Q6());
        dto.setD0150Phq9Q7(assessment.getD0150Phq9Q7());
        dto.setD0150Phq9Q8(assessment.getD0150Phq9Q8());
        dto.setD0150Phq9Q9(assessment.getD0150Phq9Q9());
        dto.setD0160Phq9TotalScore(assessment.getD0160Phq9TotalScore());

        // Section 16
        dto.setM1041InfluenzaVaccinePeriod(assessment.getM1041InfluenzaVaccinePeriod());
        dto.setM1046InfluenzaVaccineReceived(assessment.getM1046InfluenzaVaccineReceived());
        dto.setM1051PneumococcalVaccine(assessment.getM1051PneumococcalVaccine());

        // Section 17
        dto.setM2401InterventionSynopsis(fromJson(assessment.getM2401InterventionSynopsis(), new TypeReference<Map<String, Object>>() {}));
        dto.setM2410DischargeTo(assessment.getM2410DischargeTo());
        dto.setM2420DischargeDisposition(assessment.getM2420DischargeDisposition());

        // Audit fields
        dto.setCreatedAt(assessment.getCreatedAt());
        dto.setUpdatedAt(assessment.getUpdatedAt());
        dto.setCreatedBy(assessment.getCreatedBy());
        dto.setUpdatedBy(assessment.getUpdatedBy());

        return dto;
    }

    /**
     * Validate user can edit assessment
     */
    private void validateCanEdit(OasisAssessment assessment) {
        UserPrincipal userPrincipal = getCurrentUser();

        // Check organization access
        if (!assessment.getOrganization().getId().equals(userPrincipal.getOrganizationId())) {
            throw new RuntimeException("Access denied");
        }

        // Check if locked
        if (assessment.isLocked()) {
            throw new RuntimeException("Assessment is locked and cannot be edited");
        }

        // Check if in editable status
        if (!assessment.canEdit()) {
            throw new RuntimeException("Assessment cannot be edited in current status: " + assessment.getStatus());
        }
    }

    /**
     * Get current authenticated user
     */
    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Check if string is not empty
     */
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Convert object to JSON string
     */
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error converting to JSON", e);
            return null;
        }
    }

    /**
     * Convert JSON string to object
     */
    private <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (json == null || json.trim().isEmpty()) return null;
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON", e);
            return null;
        }
    }
}

