package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByMedicalRecordNumber(String medicalRecordNumber);

    Boolean existsByMedicalRecordNumber(String medicalRecordNumber);

    @Query("SELECT p FROM Patient p WHERE p.organization.id = :organizationId AND p.isActive = true AND p.isDeleted = false ORDER BY p.lastName, p.firstName")
    List<Patient> findByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT p FROM Patient p WHERE p.organization.id = :organizationId AND p.status = :status AND p.isActive = true AND p.isDeleted = false ORDER BY p.lastName, p.firstName")
    List<Patient> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    @Query("SELECT p FROM Patient p WHERE p.organization.id = :organizationId AND " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.medicalRecordNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "p.isActive = true AND p.isDeleted = false ORDER BY p.lastName, p.firstName")
    List<Patient> searchPatients(@Param("organizationId") Long organizationId, @Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM Patient p WHERE p.admittingClinician.id = :clinicianId AND p.isActive = true AND p.isDeleted = false ORDER BY p.lastName, p.firstName")
    List<Patient> findByAdmittingClinicianId(@Param("clinicianId") Long clinicianId);

    @Query("SELECT p FROM Patient p WHERE p.organization.id = :organizationId AND p.admittingClinician.id = :clinicianId AND p.isActive = true AND p.isDeleted = false ORDER BY p.lastName, p.firstName")
    List<Patient> findByOrganizationIdAndAdmittingClinicianId(@Param("organizationId") Long organizationId, @Param("clinicianId") Long clinicianId);

    @Query("SELECT p FROM Patient p WHERE p.organization.id = :organizationId AND p.admittingClinician.id = :clinicianId AND " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.medicalRecordNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "p.isActive = true AND p.isDeleted = false ORDER BY p.lastName, p.firstName")
    List<Patient> searchPatientsByOrganizationAndClinician(@Param("organizationId") Long organizationId, 
                                                             @Param("clinicianId") Long clinicianId, 
                                                             @Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM Patient p WHERE p.organization.id = :organizationId AND p.admissionDate BETWEEN :startDate AND :endDate AND p.isActive = true AND p.isDeleted = false ORDER BY p.admissionDate DESC")
    List<Patient> findByOrganizationIdAndAdmissionDateBetween(@Param("organizationId") Long organizationId, 
                                                                @Param("startDate") LocalDate startDate, 
                                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.organization.id = :organizationId AND p.status = 'ACTIVE' AND p.isActive = true AND p.isDeleted = false")
    Long countActivePatientsByOrganization(@Param("organizationId") Long organizationId);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.organization.id = :organizationId AND p.status = 'PENDING' AND p.isActive = true AND p.isDeleted = false")
    Long countPendingPatientsByOrganization(@Param("organizationId") Long organizationId);
}

