package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.OasisAssessmentComplete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Complete OASIS-E1 Assessment
 */
@Repository
public interface OasisAssessmentCompleteRepository extends JpaRepository<OasisAssessmentComplete, Long> {

    /**
     * Find all assessments for a patient
     */
    @Query("SELECT o FROM OasisAssessmentComplete o WHERE o.patient.id = :patientId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<OasisAssessmentComplete> findByPatientIdAndDeletedFalse(@Param("patientId") Long patientId);

    /**
     * Find all assessments for an episode
     */
    @Query("SELECT o FROM OasisAssessmentComplete o WHERE o.episode.id = :episodeId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<OasisAssessmentComplete> findByEpisodeIdAndDeletedFalse(@Param("episodeId") Long episodeId);

    /**
     * Find all assessments for an organization
     */
    @Query("SELECT o FROM OasisAssessmentComplete o WHERE o.organization.id = :organizationId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<OasisAssessmentComplete> findByOrganizationIdAndDeletedFalse(@Param("organizationId") Long organizationId);

    /**
     * Find assessments by status
     */
    @Query("SELECT o FROM OasisAssessmentComplete o WHERE o.status = :status AND o.organization.id = :organizationId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<OasisAssessmentComplete> findByStatusAndOrganizationId(@Param("status") String status, @Param("organizationId") Long organizationId);

    /**
     * Find incomplete assessments
     */
    @Query("SELECT o FROM OasisAssessmentComplete o WHERE o.completionPercentage < 100 AND o.organization.id = :organizationId AND o.isDeleted = false ORDER BY o.lastAutoSaved DESC")
    List<OasisAssessmentComplete> findIncompleteByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * Find assessments pending QA review
     */
    @Query("SELECT o FROM OasisAssessmentComplete o WHERE o.status = 'SUBMITTED' AND o.organization.id = :organizationId AND o.isDeleted = false ORDER BY o.submittedAt ASC")
    List<OasisAssessmentComplete> findPendingQAReviewByOrganizationId(@Param("organizationId") Long organizationId);

    /**
     * Find assessment by ID and organization
     */
    @Query("SELECT o FROM OasisAssessmentComplete o WHERE o.id = :id AND o.organization.id = :organizationId AND o.isDeleted = false")
    Optional<OasisAssessmentComplete> findByIdAndOrganizationId(@Param("id") Long id, @Param("organizationId") Long organizationId);
}

