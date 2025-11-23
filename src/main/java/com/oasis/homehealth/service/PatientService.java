package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.*;
import com.oasis.homehealth.entity.*;
import com.oasis.homehealth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public PatientDTO createPatient(PatientRequest request, Long organizationId) {
        // Validate organization
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Generate MRN
        String mrn = generateMRN(organization);

        // Create patient
        Patient patient = Patient.builder()
                .organization(organization)
                .medicalRecordNumber(mrn)
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .ssn(request.getSsn())
                .maritalStatus(request.getMaritalStatus())
                .race(request.getRace())
                .ethnicity(request.getEthnicity())
                .language(request.getLanguage())
                .phoneNumber(request.getPhoneNumber())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .county(request.getCounty())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactRelationship(request.getEmergencyContactRelationship())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .primaryPhysicianName(request.getPrimaryPhysicianName())
                .primaryPhysicianPhone(request.getPrimaryPhysicianPhone())
                .primaryPhysicianNpi(request.getPrimaryPhysicianNpi())
                .referralSource(request.getReferralSource())
                .referralDate(request.getReferralDate())
                .referralDiagnosis(request.getReferralDiagnosis())
                .admissionDate(request.getAdmissionDate())
                .status(request.getStatus())
                .statusReason(request.getStatusReason())
                .allergies(request.getAllergies())
                .medications(request.getMedications())
                .medicalHistory(request.getMedicalHistory())
                .specialInstructions(request.getSpecialInstructions())
                .build();
        
        patient.setIsActive(true);
        patient.setIsDeleted(false);

        // Set admitting clinician if provided
        if (request.getAdmittingClinicianId() != null) {
            User clinician = userRepository.findById(request.getAdmittingClinicianId())
                    .orElseThrow(() -> new RuntimeException("Clinician not found"));
            patient.setAdmittingClinician(clinician);
        }

        Patient savedPatient = patientRepository.save(patient);
        
        // Auto-create initial episode and OASIS task (first task for every patient)
        createInitialEpisodeAndOasisTask(savedPatient, organization, request.getAdmittingClinicianId());
        
        return convertToDTO(savedPatient);
    }
    
    /**
     * Auto-create initial episode and "Start OASIS Assessment" task for new patient
     * This is the default first task for every patient
     */
    private void createInitialEpisodeAndOasisTask(Patient patient, Organization organization, Long admittingClinicianId) {
        try {
            // 1. Create initial episode
            String episodeNumber = generateEpisodeNumber(organization, patient);
            Episode initialEpisode = Episode.builder()
                    .patient(patient)
                    .organization(organization)
                    .episodeNumber(episodeNumber)
                    .episodeType("INITIAL")
                    .startDate(LocalDate.now())
                    .certificationStartDate(LocalDate.now())
                    .certificationEndDate(LocalDate.now().plusDays(60)) // 60-day certification period
                    .certificationPeriod(1)
                    .status("ACTIVE")
                    // Primary diagnosis will be set when OASIS assessment is completed
                    // Physician name can be set from patient's primary physician if available
                    .physicianName(patient.getPrimaryPhysicianName())
                    .physicianNpi(patient.getPrimaryPhysicianNpi())
                    .physicianPhone(patient.getPrimaryPhysicianPhone())
                    .build();
            initialEpisode.setIsDeleted(false);
            
            // Set primary nurse if admitting clinician is provided
            if (admittingClinicianId != null) {
                User clinician = userRepository.findById(admittingClinicianId).orElse(null);
                if (clinician != null) {
                    initialEpisode.setPrimaryNurse(clinician);
                }
            }
            
            Episode savedEpisode = episodeRepository.save(initialEpisode);
            
            // 2. Create "Start OASIS Assessment" task
            String taskNumber = generateTaskNumber(organization);
            Task oasisTask = Task.builder()
                    .patient(patient)
                    .episode(savedEpisode)
                    .organization(organization)
                    .taskNumber(taskNumber)
                    .taskType("OASIS_ASSESSMENT")
                    .title("Start OASIS Assessment - SOC")
                    .description("Complete Start of Care (SOC) OASIS-E1 assessment for patient: " + patient.getFullName())
                    .scheduledDate(LocalDate.now())
                    .status("PENDING")
                    .priority("HIGH")
                    .isUrgent(false)
                    .build();
            
            // Assign to admitting clinician if provided
            if (admittingClinicianId != null) {
                User clinician = userRepository.findById(admittingClinicianId).orElse(null);
                if (clinician != null) {
                    oasisTask.setAssignedTo(clinician);
                }
            }
            
            oasisTask.setIsDeleted(false);
            taskRepository.save(oasisTask);
            
        } catch (Exception e) {
            // Log error but don't fail patient creation
            // Task creation can be done manually later if auto-creation fails
            System.err.println("Warning: Failed to auto-create episode/task for patient " + patient.getId() + ": " + e.getMessage());
        }
    }
    
    private String generateEpisodeNumber(Organization organization, Patient patient) {
        String prefix = organization.getOrganizationCode();
        long count = episodeRepository.count() + 1;
        return String.format("%s-E%06d", prefix, count);
    }
    
    private String generateTaskNumber(Organization organization) {
        String prefix = organization.getOrganizationCode();
        long count = taskRepository.count() + 1;
        return String.format("%s-T%08d", prefix, count);
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Update fields
        patient.setFirstName(request.getFirstName());
        patient.setMiddleName(request.getMiddleName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setSsn(request.getSsn());
        patient.setMaritalStatus(request.getMaritalStatus());
        patient.setRace(request.getRace());
        patient.setEthnicity(request.getEthnicity());
        patient.setLanguage(request.getLanguage());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setMobileNumber(request.getMobileNumber());
        patient.setEmail(request.getEmail());
        patient.setAddressLine1(request.getAddressLine1());
        patient.setAddressLine2(request.getAddressLine2());
        patient.setCity(request.getCity());
        patient.setState(request.getState());
        patient.setZipCode(request.getZipCode());
        patient.setCounty(request.getCounty());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patient.setPrimaryPhysicianName(request.getPrimaryPhysicianName());
        patient.setPrimaryPhysicianPhone(request.getPrimaryPhysicianPhone());
        patient.setPrimaryPhysicianNpi(request.getPrimaryPhysicianNpi());
        patient.setReferralSource(request.getReferralSource());
        patient.setReferralDate(request.getReferralDate());
        patient.setReferralDiagnosis(request.getReferralDiagnosis());
        patient.setAdmissionDate(request.getAdmissionDate());
        patient.setStatus(request.getStatus());
        patient.setStatusReason(request.getStatusReason());
        patient.setAllergies(request.getAllergies());
        patient.setMedications(request.getMedications());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setSpecialInstructions(request.getSpecialInstructions());

        // Update admitting clinician if provided
        if (request.getAdmittingClinicianId() != null) {
            User clinician = userRepository.findById(request.getAdmittingClinicianId())
                    .orElseThrow(() -> new RuntimeException("Clinician not found"));
            patient.setAdmittingClinician(clinician);
        }

        Patient updatedPatient = patientRepository.save(patient);
        return convertToDTO(updatedPatient);
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return convertToDTO(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatientsByOrganization(Long organizationId) {
        List<Patient> patients = patientRepository.findByOrganizationId(organizationId);
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get patients by organization and clinician (for RN/PT/OT/ST roles)
     * Shows only patients assigned to the specific clinician
     */
    @Transactional(readOnly = true)
    public List<PatientDTO> getPatientsByOrganizationAndClinician(Long organizationId, Long clinicianId) {
        List<Patient> patients = patientRepository.findByOrganizationIdAndAdmittingClinicianId(organizationId, clinicianId);
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> searchPatients(Long organizationId, String searchTerm) {
        List<Patient> patients = patientRepository.searchPatients(organizationId, searchTerm);
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search patients by organization and clinician (for RN/PT/OT/ST roles)
     * Shows only patients assigned to the specific clinician
     */
    @Transactional(readOnly = true)
    public List<PatientDTO> searchPatientsByOrganizationAndClinician(Long organizationId, Long clinicianId, String searchTerm) {
        List<Patient> patients = patientRepository.searchPatientsByOrganizationAndClinician(organizationId, clinicianId, searchTerm);
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getPatientsByStatus(Long organizationId, String status) {
        List<Patient> patients = patientRepository.findByOrganizationIdAndStatus(organizationId, status);
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setIsDeleted(true);
        patient.setIsActive(false);
        patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Long countActivePatients(Long organizationId) {
        return patientRepository.countActivePatientsByOrganization(organizationId);
    }

    @Transactional(readOnly = true)
    public Long countPendingPatients(Long organizationId) {
        return patientRepository.countPendingPatientsByOrganization(organizationId);
    }

    // Helper methods
    private String generateMRN(Organization organization) {
        String prefix = organization.getOrganizationCode();
        long count = patientRepository.count() + 1;
        return String.format("%s-P%06d", prefix, count);
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = PatientDTO.builder()
                .id(patient.getId())
                .medicalRecordNumber(patient.getMedicalRecordNumber())
                .firstName(patient.getFirstName())
                .middleName(patient.getMiddleName())
                .lastName(patient.getLastName())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .age(patient.getAge())
                .gender(patient.getGender())
                .ssn(patient.getSsn())
                .maritalStatus(patient.getMaritalStatus())
                .race(patient.getRace())
                .ethnicity(patient.getEthnicity())
                .language(patient.getLanguage())
                .phoneNumber(patient.getPhoneNumber())
                .mobileNumber(patient.getMobileNumber())
                .email(patient.getEmail())
                .addressLine1(patient.getAddressLine1())
                .addressLine2(patient.getAddressLine2())
                .city(patient.getCity())
                .state(patient.getState())
                .zipCode(patient.getZipCode())
                .county(patient.getCounty())
                .fullAddress(patient.getFullAddress())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactRelationship(patient.getEmergencyContactRelationship())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .primaryPhysicianName(patient.getPrimaryPhysicianName())
                .primaryPhysicianPhone(patient.getPrimaryPhysicianPhone())
                .primaryPhysicianNpi(patient.getPrimaryPhysicianNpi())
                .referralSource(patient.getReferralSource())
                .referralDate(patient.getReferralDate())
                .referralDiagnosis(patient.getReferralDiagnosis())
                .admissionDate(patient.getAdmissionDate())
                .dischargeDate(patient.getDischargeDate())
                .status(patient.getStatus())
                .statusReason(patient.getStatusReason())
                .allergies(patient.getAllergies())
                .medications(patient.getMedications())
                .medicalHistory(patient.getMedicalHistory())
                .specialInstructions(patient.getSpecialInstructions())
                .organizationId(patient.getOrganization().getId())
                .organizationName(patient.getOrganization().getOrganizationName())
                .createdAt(patient.getCreatedAt() != null ? patient.getCreatedAt().toLocalDate() : null)
                .createdBy(patient.getCreatedBy())
                .updatedAt(patient.getUpdatedAt() != null ? patient.getUpdatedAt().toLocalDate() : null)
                .updatedBy(patient.getUpdatedBy())
                .build();

        // Set admitting clinician
        if (patient.getAdmittingClinician() != null) {
            dto.setAdmittingClinicianId(patient.getAdmittingClinician().getId());
            dto.setAdmittingClinicianName(patient.getAdmittingClinician().getFullName());
        }

        return dto;
    }
}

