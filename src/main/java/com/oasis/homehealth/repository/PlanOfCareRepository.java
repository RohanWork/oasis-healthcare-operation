package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.PlanOfCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanOfCareRepository extends JpaRepository<PlanOfCare, Long> {

    // Find by POC number
    Optional<PlanOfCare> findByPocNumber(String pocNumber);

    Boolean existsByPocNumber(String pocNumber);

    // Find by patient
    @Query("SELECT p FROM PlanOfCare p WHERE p.patient.id = :patientId AND p.isDeleted = false ORDER BY p.startDate DESC")
    List<PlanOfCare> findByPatientId(@Param("patientId") Long patientId);

    // Find by episode
    @Query("SELECT p FROM PlanOfCare p WHERE p.episode.id = :episodeId AND p.isDeleted = false ORDER BY p.startDate DESC")
    List<PlanOfCare> findByEpisodeId(@Param("episodeId") Long episodeId);

    // Find by organization
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId AND p.isDeleted = false ORDER BY p.startDate DESC")
    List<PlanOfCare> findByOrganizationId(@Param("organizationId") Long organizationId);

    // Find by organization and status
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId AND p.status = :status AND p.isDeleted = false ORDER BY p.startDate DESC")
    List<PlanOfCare> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    // Find by OASIS assessment
    @Query("SELECT p FROM PlanOfCare p WHERE p.oasisAssessment.id = :oasisId AND p.isDeleted = false")
    Optional<PlanOfCare> findByOasisAssessmentId(@Param("oasisId") Long oasisId);

    // Find active POC for patient
    @Query("SELECT p FROM PlanOfCare p WHERE p.patient.id = :patientId AND p.status = 'ACTIVE' AND p.isDeleted = false ORDER BY p.startDate DESC")
    Optional<PlanOfCare> findActiveByPatientId(@Param("patientId") Long patientId);

    // Find active POC for episode
    @Query("SELECT p FROM PlanOfCare p WHERE p.episode.id = :episodeId AND p.status = 'ACTIVE' AND p.isDeleted = false ORDER BY p.startDate DESC")
    Optional<PlanOfCare> findActiveByEpisodeId(@Param("episodeId") Long episodeId);

    // Find POCs created by clinician
    @Query("SELECT p FROM PlanOfCare p WHERE p.createdByClinician.id = :clinicianId AND p.isDeleted = false ORDER BY p.startDate DESC")
    List<PlanOfCare> findByCreatedByClinician(@Param("clinicianId") Long clinicianId);

    // Find POCs pending approval
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId AND p.status = 'PENDING_APPROVAL' AND p.isDeleted = false ORDER BY p.createdAt ASC")
    List<PlanOfCare> findPendingApproval(@Param("organizationId") Long organizationId);

    // Find expiring POCs (end date within next 7 days)
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId " +
           "AND p.status = 'ACTIVE' " +
           "AND p.endDate BETWEEN :startDate AND :endDate " +
           "AND p.isDeleted = false ORDER BY p.endDate ASC")
    List<PlanOfCare> findExpiringPOCs(@Param("organizationId") Long organizationId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // Find expired POCs
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId " +
           "AND p.status = 'ACTIVE' " +
           "AND p.endDate < :currentDate " +
           "AND p.isDeleted = false ORDER BY p.endDate DESC")
    List<PlanOfCare> findExpiredPOCs(@Param("organizationId") Long organizationId,
                                      @Param("currentDate") LocalDate currentDate);

    // Find POCs needing physician signature
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId " +
           "AND p.physicianSignatureRequired = true " +
           "AND (p.physicianSignatureObtained = false OR p.physicianSignatureObtained IS NULL) " +
           "AND p.isDeleted = false ORDER BY p.startDate ASC")
    List<PlanOfCare> findNeedingPhysicianSignature(@Param("organizationId") Long organizationId);

    // Count by status for organization
    @Query("SELECT COUNT(p) FROM PlanOfCare p WHERE p.organization.id = :organizationId AND p.status = :status AND p.isDeleted = false")
    Long countByStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    // Find by date range
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId " +
           "AND p.startDate BETWEEN :startDate AND :endDate " +
           "AND p.isDeleted = false ORDER BY p.startDate DESC")
    List<PlanOfCare> findByDateRange(@Param("organizationId") Long organizationId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // Find by organization and clinician (for role-based filtering)
    @Query("SELECT p FROM PlanOfCare p WHERE p.organization.id = :organizationId " +
           "AND p.patient.admittingClinician.id = :clinicianId " +
           "AND p.isDeleted = false ORDER BY p.startDate DESC")
    List<PlanOfCare> findByOrganizationAndClinician(@Param("organizationId") Long organizationId,
                                                      @Param("clinicianId") Long clinicianId);
}

