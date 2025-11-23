package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.VisitNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitNoteRepository extends JpaRepository<VisitNote, Long> {

    // Find by task
    @Query("SELECT v FROM VisitNote v WHERE v.task.id = :taskId AND v.isDeleted = false")
    Optional<VisitNote> findByTaskId(@Param("taskId") Long taskId);

    // Find by patient
    @Query("SELECT v FROM VisitNote v WHERE v.patient.id = :patientId AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitNote> findByPatientId(@Param("patientId") Long patientId);

    // Find by episode
    @Query("SELECT v FROM VisitNote v WHERE v.episode.id = :episodeId AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitNote> findByEpisodeId(@Param("episodeId") Long episodeId);

    // Find by clinician
    @Query("SELECT v FROM VisitNote v WHERE v.clinician.id = :clinicianId AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitNote> findByClinicianId(@Param("clinicianId") Long clinicianId);

    // Find by organization and status
    @Query("SELECT v FROM VisitNote v WHERE v.organization.id = :organizationId " +
           "AND v.status = :status AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitNote> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, 
                                                    @Param("status") String status);

    // Find pending QA review
    @Query("SELECT v FROM VisitNote v WHERE v.organization.id = :organizationId " +
           "AND v.status = 'SUBMITTED' AND v.isDeleted = false ORDER BY v.submittedAt ASC")
    List<VisitNote> findPendingQAReview(@Param("organizationId") Long organizationId);

    // Find all pending QA review (for SYSTEM_ADMIN)
    @Query("SELECT v FROM VisitNote v WHERE v.status = 'SUBMITTED' AND v.isDeleted = false ORDER BY v.submittedAt ASC")
    List<VisitNote> findAllPendingQAReview();

    // Find by date range
    @Query("SELECT v FROM VisitNote v WHERE v.organization.id = :organizationId " +
           "AND v.visitDate BETWEEN :startDate AND :endDate " +
           "AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitNote> findByOrganizationIdAndDateRange(@Param("organizationId") Long organizationId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    // Count by status
    @Query("SELECT COUNT(v) FROM VisitNote v WHERE v.organization.id = :organizationId " +
           "AND v.status = :status AND v.isDeleted = false")
    Long countByStatus(@Param("organizationId") Long organizationId, @Param("status") String status);
}

