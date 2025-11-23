package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.OasisAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OasisAssessmentRepository extends JpaRepository<OasisAssessment, Long> {

    // Find by patient
    List<OasisAssessment> findByPatientIdAndIsDeletedFalse(Long patientId);

    // Find by episode
    List<OasisAssessment> findByEpisodeIdAndIsDeletedFalse(Long episodeId);

    // Find by organization
    List<OasisAssessment> findByOrganizationIdAndIsDeletedFalse(Long organizationId);

    // Find by status
    List<OasisAssessment> findByStatusAndIsDeletedFalse(String status);

    // Find by organization and status
    List<OasisAssessment> findByOrganizationIdAndStatusAndIsDeletedFalse(Long organizationId, String status);

    // Find by assessment type
    List<OasisAssessment> findByAssessmentTypeAndIsDeletedFalse(String assessmentType);

    // Find by patient and type
    List<OasisAssessment> findByPatientIdAndAssessmentTypeAndIsDeletedFalse(Long patientId, String assessmentType);

    // Find by clinician
    List<OasisAssessment> findByClinicianIdAndIsDeletedFalse(Long clinicianId);

    // Find pending QA reviews for organization
    @Query("SELECT o FROM OasisAssessment o WHERE o.organization.id = :orgId " +
           "AND o.status = 'SUBMITTED' AND o.isDeleted = false " +
           "ORDER BY o.submittedAt ASC")
    List<OasisAssessment> findPendingQAReviews(@Param("orgId") Long organizationId);

    // Find all pending QA reviews (for SYSTEM_ADMIN)
    @Query("SELECT o FROM OasisAssessment o WHERE o.status = 'SUBMITTED' AND o.isDeleted = false " +
           "ORDER BY o.submittedAt ASC")
    List<OasisAssessment> findAllPendingQAReviews();

    // Find assessments by date range
    @Query("SELECT o FROM OasisAssessment o WHERE o.organization.id = :orgId " +
           "AND o.assessmentDate BETWEEN :startDate AND :endDate " +
           "AND o.isDeleted = false ORDER BY o.assessmentDate DESC")
    List<OasisAssessment> findByDateRange(
        @Param("orgId") Long organizationId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Find incomplete assessments (DRAFT status)
    @Query("SELECT o FROM OasisAssessment o WHERE o.organization.id = :orgId " +
           "AND o.status = 'DRAFT' AND o.isDeleted = false " +
           "ORDER BY o.updatedAt DESC")
    List<OasisAssessment> findIncompleteAssessments(@Param("orgId") Long organizationId);

    // Find locked assessments
    @Query("SELECT o FROM OasisAssessment o WHERE o.organization.id = :orgId " +
           "AND o.status = 'LOCKED' AND o.isDeleted = false " +
           "ORDER BY o.lockedAt DESC")
    List<OasisAssessment> findLockedAssessments(@Param("orgId") Long organizationId);

    // Check if patient has existing SOC assessment
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
           "FROM OasisAssessment o WHERE o.patient.id = :patientId " +
           "AND o.assessmentType = 'SOC' AND o.isDeleted = false")
    boolean hasExistingSOC(@Param("patientId") Long patientId);

    // Get latest assessment for patient
    @Query("SELECT o FROM OasisAssessment o WHERE o.patient.id = :patientId " +
           "AND o.isDeleted = false ORDER BY o.assessmentDate DESC")
    Optional<OasisAssessment> findLatestByPatient(@Param("patientId") Long patientId);

    // Count by status for organization
    @Query("SELECT COUNT(o) FROM OasisAssessment o WHERE o.organization.id = :orgId " +
           "AND o.status = :status AND o.isDeleted = false")
    Long countByStatus(@Param("orgId") Long organizationId, @Param("status") String status);

    // Get assessments needing attention (incomplete or rejected)
    @Query("SELECT o FROM OasisAssessment o WHERE o.organization.id = :orgId " +
           "AND o.status IN ('DRAFT', 'REJECTED') AND o.isDeleted = false " +
           "ORDER BY o.updatedAt DESC")
    List<OasisAssessment> findAssessmentsNeedingAttention(@Param("orgId") Long organizationId);

    // Find by clinician and status
    List<OasisAssessment> findByClinicianIdAndStatusAndIsDeletedFalse(Long clinicianId, String status);
}

