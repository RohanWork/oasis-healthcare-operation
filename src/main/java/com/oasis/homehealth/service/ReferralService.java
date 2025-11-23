package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.ReferralDTO;
import com.oasis.homehealth.dto.ReferralRequest;
import com.oasis.homehealth.entity.Organization;
import com.oasis.homehealth.entity.Referral;
import com.oasis.homehealth.repository.OrganizationRepository;
import com.oasis.homehealth.repository.ReferralRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final OrganizationRepository organizationRepository;

    public List<ReferralDTO> getAllReferrals(Long organizationId) {
        log.info("Getting all referrals for organization: {}", organizationId);
        List<Referral> referrals = referralRepository.findByOrganizationId(organizationId);
        return referrals.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReferralDTO getReferralById(Long id, Long organizationId) {
        log.info("Getting referral: {} for organization: {}", id, organizationId);
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Referral not found"));
        
        // Validate organization access
        if (!referral.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }
        
        return mapToDTO(referral);
    }

    public ReferralDTO createReferral(ReferralRequest request, Long organizationId) {
        log.info("Creating referral for organization: {}", organizationId);
        
        // Validate organization
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        // Generate referral number if not provided
        String referralNumber = request.getReferralNumber();
        if (referralNumber == null || referralNumber.trim().isEmpty()) {
            referralNumber = generateReferralNumber(organization);
        }
        
        // Create referral
        Referral referral = mapRequestToEntity(request, organization, referralNumber);
        referral = referralRepository.save(referral);
        
        log.info("Referral created: {}", referral.getId());
        return mapToDTO(referral);
    }

    public ReferralDTO updateReferral(Long id, ReferralRequest request, Long organizationId) {
        log.info("Updating referral: {} for organization: {}", id, organizationId);
        
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Referral not found"));
        
        // Validate organization access
        if (!referral.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }
        
        // Update fields
        updateEntityFromRequest(referral, request);
        referral = referralRepository.save(referral);
        
        log.info("Referral updated: {}", id);
        return mapToDTO(referral);
    }

    public void deleteReferral(Long id, Long organizationId) {
        log.info("Deleting referral: {} for organization: {}", id, organizationId);
        
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Referral not found"));
        
        // Validate organization access
        if (!referral.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Access denied");
        }
        
        // Soft delete
        referral.setIsDeleted(true);
        referral.setIsActive(false);
        referralRepository.save(referral);
        
        log.info("Referral deleted: {}", id);
    }

    private String generateReferralNumber(Organization organization) {
        String orgCode = organization.getOrganizationCode() != null 
            ? organization.getOrganizationCode().toUpperCase() 
            : "ORG";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = referralRepository.count() + 1;
        return String.format("REF-%s-%s-%04d", orgCode, dateStr, count);
    }

    private Referral mapRequestToEntity(ReferralRequest request, Organization organization, String referralNumber) {
        Referral.ReferralBuilder builder = Referral.builder()
                .organization(organization)
                .referralNumber(referralNumber)
                .referralDate(request.getReferralDate())
                .referralSource(request.getReferralSource())
                .referralSourceContact(request.getReferralSourceContact())
                .referralSourcePhone(request.getReferralSourcePhone())
                .referralSourceEmail(request.getReferralSourceEmail())
                .referralSourceFax(request.getReferralSourceFax())
                .referralSourceAddress(request.getReferralSourceAddress())
                .referralSourceCity(request.getReferralSourceCity())
                .referralSourceState(request.getReferralSourceState())
                .referralSourceZip(request.getReferralSourceZip())
                .patientFirstName(request.getPatientFirstName())
                .patientMiddleName(request.getPatientMiddleName())
                .patientLastName(request.getPatientLastName())
                .patientDateOfBirth(request.getPatientDateOfBirth())
                .patientGender(request.getPatientGender())
                .patientSsn(request.getPatientSsn())
                .patientMaritalStatus(request.getPatientMaritalStatus())
                .patientRace(request.getPatientRace())
                .patientEthnicity(request.getPatientEthnicity())
                .patientLanguage(request.getPatientLanguage())
                .patientPhoneNumber(request.getPatientPhoneNumber())
                .patientMobileNumber(request.getPatientMobileNumber())
                .patientEmail(request.getPatientEmail())
                .patientAddressLine1(request.getPatientAddressLine1())
                .patientAddressLine2(request.getPatientAddressLine2())
                .patientCity(request.getPatientCity())
                .patientState(request.getPatientState())
                .patientZipCode(request.getPatientZipCode())
                .patientCounty(request.getPatientCounty())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactRelationship(request.getEmergencyContactRelationship())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .primaryInsurance(request.getPrimaryInsurance())
                .primaryInsuranceId(request.getPrimaryInsuranceId())
                .primaryInsuranceGroup(request.getPrimaryInsuranceGroup())
                .primaryInsurancePhone(request.getPrimaryInsurancePhone())
                .secondaryInsurance(request.getSecondaryInsurance())
                .secondaryInsuranceId(request.getSecondaryInsuranceId())
                .secondaryInsuranceGroup(request.getSecondaryInsuranceGroup())
                .medicaidNumber(request.getMedicaidNumber())
                .medicareNumber(request.getMedicareNumber())
                .primaryPhysicianName(request.getPrimaryPhysicianName())
                .primaryPhysicianNpi(request.getPrimaryPhysicianNpi())
                .primaryPhysicianPhone(request.getPrimaryPhysicianPhone())
                .primaryPhysicianFax(request.getPrimaryPhysicianFax())
                .primaryPhysicianAddress(request.getPrimaryPhysicianAddress())
                .primaryPhysicianCity(request.getPrimaryPhysicianCity())
                .primaryPhysicianState(request.getPrimaryPhysicianState())
                .primaryPhysicianZip(request.getPrimaryPhysicianZip())
                .referringPhysicianName(request.getReferringPhysicianName())
                .referringPhysicianNpi(request.getReferringPhysicianNpi())
                .referringPhysicianPhone(request.getReferringPhysicianPhone())
                .primaryDiagnosis(request.getPrimaryDiagnosis())
                .primaryDiagnosisIcd10(request.getPrimaryDiagnosisIcd10())
                .primaryDiagnosisDescription(request.getPrimaryDiagnosisDescription())
                .secondaryDiagnosis1(request.getSecondaryDiagnosis1())
                .secondaryDiagnosis1Icd10(request.getSecondaryDiagnosis1Icd10())
                .secondaryDiagnosis2(request.getSecondaryDiagnosis2())
                .secondaryDiagnosis2Icd10(request.getSecondaryDiagnosis2Icd10())
                .secondaryDiagnosis3(request.getSecondaryDiagnosis3())
                .secondaryDiagnosis3Icd10(request.getSecondaryDiagnosis3Icd10())
                .serviceRequested(request.getServiceRequested())
                .serviceStartDate(request.getServiceStartDate())
                .expectedFrequency(request.getExpectedFrequency())
                .expectedDuration(request.getExpectedDuration())
                .specialInstructions(request.getSpecialInstructions())
                .allergies(request.getAllergies())
                .currentMedications(request.getCurrentMedications())
                .medicalHistory(request.getMedicalHistory())
                .functionalLimitations(request.getFunctionalLimitations())
                .equipmentNeeds(request.getEquipmentNeeds())
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .notes(request.getNotes());
        
        return builder.build();
    }

    private void updateEntityFromRequest(Referral referral, ReferralRequest request) {
        referral.setReferralDate(request.getReferralDate());
        referral.setReferralSource(request.getReferralSource());
        referral.setReferralSourceContact(request.getReferralSourceContact());
        referral.setReferralSourcePhone(request.getReferralSourcePhone());
        referral.setReferralSourceEmail(request.getReferralSourceEmail());
        referral.setReferralSourceFax(request.getReferralSourceFax());
        referral.setReferralSourceAddress(request.getReferralSourceAddress());
        referral.setReferralSourceCity(request.getReferralSourceCity());
        referral.setReferralSourceState(request.getReferralSourceState());
        referral.setReferralSourceZip(request.getReferralSourceZip());
        referral.setPatientFirstName(request.getPatientFirstName());
        referral.setPatientMiddleName(request.getPatientMiddleName());
        referral.setPatientLastName(request.getPatientLastName());
        referral.setPatientDateOfBirth(request.getPatientDateOfBirth());
        referral.setPatientGender(request.getPatientGender());
        referral.setPatientSsn(request.getPatientSsn());
        referral.setPatientMaritalStatus(request.getPatientMaritalStatus());
        referral.setPatientRace(request.getPatientRace());
        referral.setPatientEthnicity(request.getPatientEthnicity());
        referral.setPatientLanguage(request.getPatientLanguage());
        referral.setPatientPhoneNumber(request.getPatientPhoneNumber());
        referral.setPatientMobileNumber(request.getPatientMobileNumber());
        referral.setPatientEmail(request.getPatientEmail());
        referral.setPatientAddressLine1(request.getPatientAddressLine1());
        referral.setPatientAddressLine2(request.getPatientAddressLine2());
        referral.setPatientCity(request.getPatientCity());
        referral.setPatientState(request.getPatientState());
        referral.setPatientZipCode(request.getPatientZipCode());
        referral.setPatientCounty(request.getPatientCounty());
        referral.setEmergencyContactName(request.getEmergencyContactName());
        referral.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        referral.setEmergencyContactPhone(request.getEmergencyContactPhone());
        referral.setPrimaryInsurance(request.getPrimaryInsurance());
        referral.setPrimaryInsuranceId(request.getPrimaryInsuranceId());
        referral.setPrimaryInsuranceGroup(request.getPrimaryInsuranceGroup());
        referral.setPrimaryInsurancePhone(request.getPrimaryInsurancePhone());
        referral.setSecondaryInsurance(request.getSecondaryInsurance());
        referral.setSecondaryInsuranceId(request.getSecondaryInsuranceId());
        referral.setSecondaryInsuranceGroup(request.getSecondaryInsuranceGroup());
        referral.setMedicaidNumber(request.getMedicaidNumber());
        referral.setMedicareNumber(request.getMedicareNumber());
        referral.setPrimaryPhysicianName(request.getPrimaryPhysicianName());
        referral.setPrimaryPhysicianNpi(request.getPrimaryPhysicianNpi());
        referral.setPrimaryPhysicianPhone(request.getPrimaryPhysicianPhone());
        referral.setPrimaryPhysicianFax(request.getPrimaryPhysicianFax());
        referral.setPrimaryPhysicianAddress(request.getPrimaryPhysicianAddress());
        referral.setPrimaryPhysicianCity(request.getPrimaryPhysicianCity());
        referral.setPrimaryPhysicianState(request.getPrimaryPhysicianState());
        referral.setPrimaryPhysicianZip(request.getPrimaryPhysicianZip());
        referral.setReferringPhysicianName(request.getReferringPhysicianName());
        referral.setReferringPhysicianNpi(request.getReferringPhysicianNpi());
        referral.setReferringPhysicianPhone(request.getReferringPhysicianPhone());
        referral.setPrimaryDiagnosis(request.getPrimaryDiagnosis());
        referral.setPrimaryDiagnosisIcd10(request.getPrimaryDiagnosisIcd10());
        referral.setPrimaryDiagnosisDescription(request.getPrimaryDiagnosisDescription());
        referral.setSecondaryDiagnosis1(request.getSecondaryDiagnosis1());
        referral.setSecondaryDiagnosis1Icd10(request.getSecondaryDiagnosis1Icd10());
        referral.setSecondaryDiagnosis2(request.getSecondaryDiagnosis2());
        referral.setSecondaryDiagnosis2Icd10(request.getSecondaryDiagnosis2Icd10());
        referral.setSecondaryDiagnosis3(request.getSecondaryDiagnosis3());
        referral.setSecondaryDiagnosis3Icd10(request.getSecondaryDiagnosis3Icd10());
        referral.setServiceRequested(request.getServiceRequested());
        referral.setServiceStartDate(request.getServiceStartDate());
        referral.setExpectedFrequency(request.getExpectedFrequency());
        referral.setExpectedDuration(request.getExpectedDuration());
        referral.setSpecialInstructions(request.getSpecialInstructions());
        referral.setAllergies(request.getAllergies());
        referral.setCurrentMedications(request.getCurrentMedications());
        referral.setMedicalHistory(request.getMedicalHistory());
        referral.setFunctionalLimitations(request.getFunctionalLimitations());
        referral.setEquipmentNeeds(request.getEquipmentNeeds());
        if (request.getStatus() != null) {
            referral.setStatus(request.getStatus());
        }
        referral.setNotes(request.getNotes());
    }

    private ReferralDTO mapToDTO(Referral referral) {
        return ReferralDTO.builder()
                .id(referral.getId())
                .referralNumber(referral.getReferralNumber())
                .referralDate(referral.getReferralDate())
                .referralSource(referral.getReferralSource())
                .referralSourceContact(referral.getReferralSourceContact())
                .referralSourcePhone(referral.getReferralSourcePhone())
                .referralSourceEmail(referral.getReferralSourceEmail())
                .referralSourceFax(referral.getReferralSourceFax())
                .referralSourceAddress(referral.getReferralSourceAddress())
                .referralSourceCity(referral.getReferralSourceCity())
                .referralSourceState(referral.getReferralSourceState())
                .referralSourceZip(referral.getReferralSourceZip())
                .patientFirstName(referral.getPatientFirstName())
                .patientMiddleName(referral.getPatientMiddleName())
                .patientLastName(referral.getPatientLastName())
                .patientDateOfBirth(referral.getPatientDateOfBirth())
                .patientGender(referral.getPatientGender())
                .patientSsn(referral.getPatientSsn())
                .patientMaritalStatus(referral.getPatientMaritalStatus())
                .patientRace(referral.getPatientRace())
                .patientEthnicity(referral.getPatientEthnicity())
                .patientLanguage(referral.getPatientLanguage())
                .patientPhoneNumber(referral.getPatientPhoneNumber())
                .patientMobileNumber(referral.getPatientMobileNumber())
                .patientEmail(referral.getPatientEmail())
                .patientAddressLine1(referral.getPatientAddressLine1())
                .patientAddressLine2(referral.getPatientAddressLine2())
                .patientCity(referral.getPatientCity())
                .patientState(referral.getPatientState())
                .patientZipCode(referral.getPatientZipCode())
                .patientCounty(referral.getPatientCounty())
                .emergencyContactName(referral.getEmergencyContactName())
                .emergencyContactRelationship(referral.getEmergencyContactRelationship())
                .emergencyContactPhone(referral.getEmergencyContactPhone())
                .primaryInsurance(referral.getPrimaryInsurance())
                .primaryInsuranceId(referral.getPrimaryInsuranceId())
                .primaryInsuranceGroup(referral.getPrimaryInsuranceGroup())
                .primaryInsurancePhone(referral.getPrimaryInsurancePhone())
                .secondaryInsurance(referral.getSecondaryInsurance())
                .secondaryInsuranceId(referral.getSecondaryInsuranceId())
                .secondaryInsuranceGroup(referral.getSecondaryInsuranceGroup())
                .medicaidNumber(referral.getMedicaidNumber())
                .medicareNumber(referral.getMedicareNumber())
                .primaryPhysicianName(referral.getPrimaryPhysicianName())
                .primaryPhysicianNpi(referral.getPrimaryPhysicianNpi())
                .primaryPhysicianPhone(referral.getPrimaryPhysicianPhone())
                .primaryPhysicianFax(referral.getPrimaryPhysicianFax())
                .primaryPhysicianAddress(referral.getPrimaryPhysicianAddress())
                .primaryPhysicianCity(referral.getPrimaryPhysicianCity())
                .primaryPhysicianState(referral.getPrimaryPhysicianState())
                .primaryPhysicianZip(referral.getPrimaryPhysicianZip())
                .referringPhysicianName(referral.getReferringPhysicianName())
                .referringPhysicianNpi(referral.getReferringPhysicianNpi())
                .referringPhysicianPhone(referral.getReferringPhysicianPhone())
                .primaryDiagnosis(referral.getPrimaryDiagnosis())
                .primaryDiagnosisIcd10(referral.getPrimaryDiagnosisIcd10())
                .primaryDiagnosisDescription(referral.getPrimaryDiagnosisDescription())
                .secondaryDiagnosis1(referral.getSecondaryDiagnosis1())
                .secondaryDiagnosis1Icd10(referral.getSecondaryDiagnosis1Icd10())
                .secondaryDiagnosis2(referral.getSecondaryDiagnosis2())
                .secondaryDiagnosis2Icd10(referral.getSecondaryDiagnosis2Icd10())
                .secondaryDiagnosis3(referral.getSecondaryDiagnosis3())
                .secondaryDiagnosis3Icd10(referral.getSecondaryDiagnosis3Icd10())
                .serviceRequested(referral.getServiceRequested())
                .serviceStartDate(referral.getServiceStartDate())
                .expectedFrequency(referral.getExpectedFrequency())
                .expectedDuration(referral.getExpectedDuration())
                .specialInstructions(referral.getSpecialInstructions())
                .allergies(referral.getAllergies())
                .currentMedications(referral.getCurrentMedications())
                .medicalHistory(referral.getMedicalHistory())
                .functionalLimitations(referral.getFunctionalLimitations())
                .equipmentNeeds(referral.getEquipmentNeeds())
                .status(referral.getStatus())
                .notes(referral.getNotes())
                .createdAt(referral.getCreatedAt())
                .updatedAt(referral.getUpdatedAt())
                .patientId(referral.getPatient() != null ? referral.getPatient().getId() : null)
                .build();
    }
}

