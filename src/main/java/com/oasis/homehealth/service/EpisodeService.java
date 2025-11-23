package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.EpisodeDTO;
import com.oasis.homehealth.dto.EpisodeRequest;
import com.oasis.homehealth.entity.*;
import com.oasis.homehealth.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EpisodeService {

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Transactional
    public EpisodeDTO createEpisode(EpisodeRequest request, Long organizationId) {
        // Validate patient
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Validate organization
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Generate episode number
        String episodeNumber = generateEpisodeNumber(organization, patient);

        // Create episode
        Episode episode = Episode.builder()
                .patient(patient)
                .organization(organization)
                .episodeNumber(episodeNumber)
                .episodeType(request.getEpisodeType())
                .startDate(request.getStartDate())
                .certificationStartDate(request.getCertificationStartDate())
                .certificationEndDate(request.getCertificationEndDate())
                .certificationPeriod(request.getCertificationPeriod())
                .primaryDiagnosisCode(request.getPrimaryDiagnosisCode())
                .primaryDiagnosisDescription(request.getPrimaryDiagnosisDescription())
                .secondaryDiagnosisCodes(request.getSecondaryDiagnosisCodes())
                .secondaryDiagnosisDescriptions(request.getSecondaryDiagnosisDescriptions())
                .admittingDiagnosis(request.getAdmittingDiagnosis())
                .surgicalProcedures(request.getSurgicalProcedures())
                .treatmentAuthorizationCode(request.getTreatmentAuthorizationCode())
                .physicianName(request.getPhysicianName())
                .physicianNpi(request.getPhysicianNpi())
                .physicianPhone(request.getPhysicianPhone())
                .physicianOrdersDate(request.getPhysicianOrdersDate())
                .status(request.getStatus())
                .statusReason(request.getStatusReason())
                .expectedFrequency(request.getExpectedFrequency())
                .totalAuthorizedVisits(request.getTotalAuthorizedVisits())
                .visitsCompleted(0)
                .admissionSource(request.getAdmissionSource())
                .admissionSourceFacility(request.getAdmissionSourceFacility())
                .clinicalNotes(request.getClinicalNotes())
                .specialInstructions(request.getSpecialInstructions())
                .isHomebound(request.getIsHomebound())
                .requiresSkilledNursing(request.getRequiresSkilledNursing())
                .requiresTherapy(request.getRequiresTherapy())
                .requiresAide(request.getRequiresAide())
                .build();
        
        episode.setIsActive(true);
        episode.setIsDeleted(false);

        // Set care team members
        if (request.getCaseManagerId() != null) {
            User caseManager = userRepository.findById(request.getCaseManagerId())
                    .orElseThrow(() -> new RuntimeException("Case manager not found"));
            episode.setCaseManager(caseManager);
        }

        if (request.getPrimaryNurseId() != null) {
            User primaryNurse = userRepository.findById(request.getPrimaryNurseId())
                    .orElseThrow(() -> new RuntimeException("Primary nurse not found"));
            episode.setPrimaryNurse(primaryNurse);
        }

        if (request.getPrimaryTherapistId() != null) {
            User primaryTherapist = userRepository.findById(request.getPrimaryTherapistId())
                    .orElseThrow(() -> new RuntimeException("Primary therapist not found"));
            episode.setPrimaryTherapist(primaryTherapist);
        }

        // Set primary insurance
        if (request.getPrimaryInsuranceId() != null) {
            Insurance insurance = insuranceRepository.findById(request.getPrimaryInsuranceId())
                    .orElseThrow(() -> new RuntimeException("Insurance not found"));
            episode.setPrimaryInsurance(insurance);
        }

        Episode savedEpisode = episodeRepository.save(episode);
        return convertToDTO(savedEpisode);
    }

    @Transactional
    public EpisodeDTO updateEpisode(Long id, EpisodeRequest request) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        // Update fields
        episode.setEpisodeType(request.getEpisodeType());
        episode.setStartDate(request.getStartDate());
        episode.setCertificationStartDate(request.getCertificationStartDate());
        episode.setCertificationEndDate(request.getCertificationEndDate());
        episode.setCertificationPeriod(request.getCertificationPeriod());
        episode.setPrimaryDiagnosisCode(request.getPrimaryDiagnosisCode());
        episode.setPrimaryDiagnosisDescription(request.getPrimaryDiagnosisDescription());
        episode.setSecondaryDiagnosisCodes(request.getSecondaryDiagnosisCodes());
        episode.setSecondaryDiagnosisDescriptions(request.getSecondaryDiagnosisDescriptions());
        episode.setAdmittingDiagnosis(request.getAdmittingDiagnosis());
        episode.setSurgicalProcedures(request.getSurgicalProcedures());
        episode.setTreatmentAuthorizationCode(request.getTreatmentAuthorizationCode());
        episode.setPhysicianName(request.getPhysicianName());
        episode.setPhysicianNpi(request.getPhysicianNpi());
        episode.setPhysicianPhone(request.getPhysicianPhone());
        episode.setPhysicianOrdersDate(request.getPhysicianOrdersDate());
        episode.setStatus(request.getStatus());
        episode.setStatusReason(request.getStatusReason());
        episode.setExpectedFrequency(request.getExpectedFrequency());
        episode.setTotalAuthorizedVisits(request.getTotalAuthorizedVisits());
        episode.setAdmissionSource(request.getAdmissionSource());
        episode.setAdmissionSourceFacility(request.getAdmissionSourceFacility());
        episode.setClinicalNotes(request.getClinicalNotes());
        episode.setSpecialInstructions(request.getSpecialInstructions());
        episode.setIsHomebound(request.getIsHomebound());
        episode.setRequiresSkilledNursing(request.getRequiresSkilledNursing());
        episode.setRequiresTherapy(request.getRequiresTherapy());
        episode.setRequiresAide(request.getRequiresAide());

        // Update care team
        if (request.getCaseManagerId() != null) {
            User caseManager = userRepository.findById(request.getCaseManagerId())
                    .orElseThrow(() -> new RuntimeException("Case manager not found"));
            episode.setCaseManager(caseManager);
        }

        if (request.getPrimaryNurseId() != null) {
            User primaryNurse = userRepository.findById(request.getPrimaryNurseId())
                    .orElseThrow(() -> new RuntimeException("Primary nurse not found"));
            episode.setPrimaryNurse(primaryNurse);
        }

        if (request.getPrimaryTherapistId() != null) {
            User primaryTherapist = userRepository.findById(request.getPrimaryTherapistId())
                    .orElseThrow(() -> new RuntimeException("Primary therapist not found"));
            episode.setPrimaryTherapist(primaryTherapist);
        }

        // Update primary insurance
        if (request.getPrimaryInsuranceId() != null) {
            Insurance insurance = insuranceRepository.findById(request.getPrimaryInsuranceId())
                    .orElseThrow(() -> new RuntimeException("Insurance not found"));
            episode.setPrimaryInsurance(insurance);
        }

        Episode updatedEpisode = episodeRepository.save(episode);
        return convertToDTO(updatedEpisode);
    }

    @Transactional(readOnly = true)
    public EpisodeDTO getEpisodeById(Long id) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        return convertToDTO(episode);
    }

    @Transactional(readOnly = true)
    public List<EpisodeDTO> getEpisodesByPatient(Long patientId) {
        List<Episode> episodes = episodeRepository.findByPatientId(patientId);
        return episodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EpisodeDTO> getEpisodesByOrganization(Long organizationId) {
        List<Episode> episodes = episodeRepository.findByOrganizationId(organizationId);
        return episodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EpisodeDTO> getExpiringEpisodes(Long organizationId, Integer daysAhead) {
        LocalDate futureDate = LocalDate.now().plusDays(daysAhead);
        List<Episode> episodes = episodeRepository.findExpiringEpisodes(organizationId, futureDate);
        return episodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEpisode(Long id) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        episode.setIsDeleted(true);
        episode.setIsActive(false);
        episodeRepository.save(episode);
    }

    // Helper methods
    /**
     * Recertify episode - create a new episode for recertification
     */
    @Transactional
    public EpisodeDTO recertifyEpisode(Long episodeId, LocalDate newCertificationStartDate, Integer certificationPeriod, Long organizationId) {
        log.info("Recertifying episode: {}", episodeId);
        
        Episode currentEpisode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        
        if (!currentEpisode.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Episode does not belong to your organization.");
        }
        
        if (!"ACTIVE".equals(currentEpisode.getStatus())) {
            throw new IllegalStateException("Only ACTIVE episodes can be recertified.");
        }
        
        // Close current episode
        currentEpisode.setStatus("COMPLETED");
        currentEpisode.setEndDate(newCertificationStartDate.minusDays(1));
        currentEpisode.setStatusReason("Recertified - New episode created");
        episodeRepository.save(currentEpisode);
        
        // Create new recertification episode
        LocalDate newCertificationEndDate = newCertificationStartDate.plusDays(certificationPeriod - 1);
        String newEpisodeNumber = generateEpisodeNumber(currentEpisode.getOrganization(), currentEpisode.getPatient());
        
        Episode newEpisode = Episode.builder()
                .patient(currentEpisode.getPatient())
                .organization(currentEpisode.getOrganization())
                .episodeNumber(newEpisodeNumber)
                .episodeType("RECERTIFICATION")
                .startDate(newCertificationStartDate)
                .certificationStartDate(newCertificationStartDate)
                .certificationEndDate(newCertificationEndDate)
                .certificationPeriod(certificationPeriod)
                .primaryDiagnosisCode(currentEpisode.getPrimaryDiagnosisCode())
                .primaryDiagnosisDescription(currentEpisode.getPrimaryDiagnosisDescription())
                .secondaryDiagnosisCodes(currentEpisode.getSecondaryDiagnosisCodes())
                .secondaryDiagnosisDescriptions(currentEpisode.getSecondaryDiagnosisDescriptions())
                .physicianName(currentEpisode.getPhysicianName())
                .physicianNpi(currentEpisode.getPhysicianNpi())
                .physicianPhone(currentEpisode.getPhysicianPhone())
                .primaryNurse(currentEpisode.getPrimaryNurse())
                .primaryTherapist(currentEpisode.getPrimaryTherapist())
                .caseManager(currentEpisode.getCaseManager())
                .primaryInsurance(currentEpisode.getPrimaryInsurance())
                .status("ACTIVE")
                .isHomebound(currentEpisode.getIsHomebound())
                .requiresSkilledNursing(currentEpisode.getRequiresSkilledNursing())
                .requiresTherapy(currentEpisode.getRequiresTherapy())
                .requiresAide(currentEpisode.getRequiresAide())
                .build();
        
        newEpisode = episodeRepository.save(newEpisode);
        log.info("Episode recertified. New episode ID: {}", newEpisode.getId());
        return convertToDTO(newEpisode);
    }
    
    /**
     * Discharge episode
     */
    @Transactional
    public EpisodeDTO dischargeEpisode(Long episodeId, String dischargeReason, String dischargeDisposition, Long organizationId) {
        log.info("Discharging episode: {}", episodeId);
        
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        
        if (!episode.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Episode does not belong to your organization.");
        }
        
        if (!"ACTIVE".equals(episode.getStatus())) {
            throw new IllegalStateException("Only ACTIVE episodes can be discharged.");
        }
        
        episode.setStatus("COMPLETED");
        episode.setEndDate(LocalDate.now());
        episode.setDischargeReason(dischargeReason);
        episode.setDischargeDisposition(dischargeDisposition);
        episode.setStatusReason("Discharged");
        
        episode = episodeRepository.save(episode);
        log.info("Episode discharged: {}", episodeId);
        return convertToDTO(episode);
    }
    
    /**
     * Archive episode
     */
    @Transactional
    public EpisodeDTO archiveEpisode(Long episodeId, Long organizationId) {
        log.info("Archiving episode: {}", episodeId);
        
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        
        if (!episode.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("Access denied: Episode does not belong to your organization.");
        }
        
        if (!"COMPLETED".equals(episode.getStatus()) && !"CANCELLED".equals(episode.getStatus())) {
            throw new IllegalStateException("Only COMPLETED or CANCELLED episodes can be archived.");
        }
        
        episode.setStatus("ARCHIVED");
        episode.setStatusReason("Archived");
        
        episode = episodeRepository.save(episode);
        log.info("Episode archived: {}", episodeId);
        return convertToDTO(episode);
    }

    private String generateEpisodeNumber(Organization organization, Patient patient) {
        String prefix = organization.getOrganizationCode();
        long count = episodeRepository.count() + 1;
        return String.format("%s-E%06d", prefix, count);
    }

    private EpisodeDTO convertToDTO(Episode episode) {
        EpisodeDTO dto = EpisodeDTO.builder()
                .id(episode.getId())
                .patientId(episode.getPatient().getId())
                .patientName(episode.getPatient().getFullName())
                .organizationId(episode.getOrganization().getId())
                .organizationName(episode.getOrganization().getOrganizationName())
                .episodeNumber(episode.getEpisodeNumber())
                .episodeType(episode.getEpisodeType())
                .episodeTitle(episode.getEpisodeTitle())
                .startDate(episode.getStartDate())
                .endDate(episode.getEndDate())
                .certificationStartDate(episode.getCertificationStartDate())
                .certificationEndDate(episode.getCertificationEndDate())
                .certificationPeriod(episode.getCertificationPeriod())
                .remainingDays(episode.getRemainingDays())
                .primaryDiagnosisCode(episode.getPrimaryDiagnosisCode())
                .primaryDiagnosisDescription(episode.getPrimaryDiagnosisDescription())
                .secondaryDiagnosisCodes(episode.getSecondaryDiagnosisCodes())
                .secondaryDiagnosisDescriptions(episode.getSecondaryDiagnosisDescriptions())
                .admittingDiagnosis(episode.getAdmittingDiagnosis())
                .surgicalProcedures(episode.getSurgicalProcedures())
                .treatmentAuthorizationCode(episode.getTreatmentAuthorizationCode())
                .physicianName(episode.getPhysicianName())
                .physicianNpi(episode.getPhysicianNpi())
                .physicianPhone(episode.getPhysicianPhone())
                .physicianOrdersDate(episode.getPhysicianOrdersDate())
                .status(episode.getStatus())
                .statusReason(episode.getStatusReason())
                .isActive(episode.getIsActive())
                .isCertificationExpiringSoon(episode.isCertificationExpiringSoon())
                .expectedFrequency(episode.getExpectedFrequency())
                .totalAuthorizedVisits(episode.getTotalAuthorizedVisits())
                .visitsCompleted(episode.getVisitsCompleted())
                .remainingVisits(episode.getRemainingVisits())
                .admissionSource(episode.getAdmissionSource())
                .admissionSourceFacility(episode.getAdmissionSourceFacility())
                .dischargeReason(episode.getDischargeReason())
                .dischargeDisposition(episode.getDischargeDisposition())
                .clinicalNotes(episode.getClinicalNotes())
                .specialInstructions(episode.getSpecialInstructions())
                .isHomebound(episode.getIsHomebound())
                .requiresSkilledNursing(episode.getRequiresSkilledNursing())
                .requiresTherapy(episode.getRequiresTherapy())
                .requiresAide(episode.getRequiresAide())
                .createdAt(episode.getCreatedAt() != null ? episode.getCreatedAt().toLocalDate() : null)
                .createdBy(episode.getCreatedBy())
                .build();

        // Set care team
        if (episode.getCaseManager() != null) {
            dto.setCaseManagerId(episode.getCaseManager().getId());
            dto.setCaseManagerName(episode.getCaseManager().getFullName());
        }

        if (episode.getPrimaryNurse() != null) {
            dto.setPrimaryNurseId(episode.getPrimaryNurse().getId());
            dto.setPrimaryNurseName(episode.getPrimaryNurse().getFullName());
        }

        if (episode.getPrimaryTherapist() != null) {
            dto.setPrimaryTherapistId(episode.getPrimaryTherapist().getId());
            dto.setPrimaryTherapistName(episode.getPrimaryTherapist().getFullName());
        }

        // Set insurance
        if (episode.getPrimaryInsurance() != null) {
            dto.setPrimaryInsuranceId(episode.getPrimaryInsurance().getId());
            dto.setPrimaryInsuranceCompany(episode.getPrimaryInsurance().getInsuranceCompany());
        }

        return dto;
    }
}

