package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find by task number
    Optional<Task> findByTaskNumber(String taskNumber);

    Boolean existsByTaskNumber(String taskNumber);

    // Find by patient
    @Query("SELECT t FROM Task t WHERE t.patient.id = :patientId AND t.isDeleted = false ORDER BY t.scheduledDate DESC")
    List<Task> findByPatientId(@Param("patientId") Long patientId);

    // Find by episode
    @Query("SELECT t FROM Task t WHERE t.episode.id = :episodeId AND t.isDeleted = false ORDER BY t.scheduledDate DESC")
    List<Task> findByEpisodeId(@Param("episodeId") Long episodeId);

    // Find by organization
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId AND t.isDeleted = false ORDER BY t.scheduledDate DESC")
    List<Task> findByOrganizationId(@Param("organizationId") Long organizationId);

    // Find by assigned clinician
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :clinicianId AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findByAssignedToId(@Param("clinicianId") Long clinicianId);

    // Find by assigned clinician and date range (for calendar view)
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :clinicianId " +
           "AND t.scheduledDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC, t.scheduledStartTime ASC")
    List<Task> findByAssignedToIdAndDateRange(@Param("clinicianId") Long clinicianId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // Find by organization and date range
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.scheduledDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC, t.scheduledStartTime ASC")
    List<Task> findByOrganizationIdAndDateRange(@Param("organizationId") Long organizationId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // Find by status
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId AND t.status = :status " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, 
                                              @Param("status") String status);

    // Find by task type
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId AND t.taskType = :taskType " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findByOrganizationIdAndTaskType(@Param("organizationId") Long organizationId, 
                                                @Param("taskType") String taskType);

    // Find tasks due today
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.scheduledDate = :today " +
           "AND t.status IN ('SCHEDULED', 'RESCHEDULED') " +
           "AND t.isDeleted = false ORDER BY t.scheduledStartTime ASC")
    List<Task> findDueToday(@Param("organizationId") Long organizationId, @Param("today") LocalDate today);

    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.scheduledDate < :today " +
           "AND t.status IN ('SCHEDULED', 'RESCHEDULED') " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findOverdue(@Param("organizationId") Long organizationId, @Param("today") LocalDate today);

    // Find tasks pending QA review
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.status = 'COMPLETED_PENDING_QA' " +
           "AND t.isDeleted = false ORDER BY t.completedAt ASC")
    List<Task> findPendingQAReview(@Param("organizationId") Long organizationId);

    // Find tasks for clinician today
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :clinicianId " +
           "AND t.scheduledDate = :today " +
           "AND t.status IN ('SCHEDULED', 'RESCHEDULED', 'IN_PROGRESS') " +
           "AND t.isDeleted = false ORDER BY t.scheduledStartTime ASC")
    List<Task> findClinicianTasksToday(@Param("clinicianId") Long clinicianId, @Param("today") LocalDate today);

    // Find unassigned tasks
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.assignedTo IS NULL " +
           "AND t.status = 'SCHEDULED' " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findUnassignedTasks(@Param("organizationId") Long organizationId);

    // Count tasks by status
    @Query("SELECT COUNT(t) FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.status = :status AND t.isDeleted = false")
    Long countByStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    // Count tasks by clinician and status
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id = :clinicianId " +
           "AND t.status = :status AND t.isDeleted = false")
    Long countByClinicianAndStatus(@Param("clinicianId") Long clinicianId, @Param("status") String status);

    // Find by Plan of Care (to see all tasks generated from a POC)
    @Query("SELECT t FROM Task t WHERE t.planOfCare.id = :pocId AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findByPlanOfCareId(@Param("pocId") Long pocId);

    // Find by organization and clinician (role-based filtering)
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.patient.admittingClinician.id = :clinicianId " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findByOrganizationAndPatientClinician(@Param("organizationId") Long organizationId,
                                                       @Param("clinicianId") Long clinicianId);

    // Find urgent tasks
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND (t.isUrgent = true OR t.priority = 'URGENT' OR t.priority = 'HIGH') " +
           "AND t.status IN ('SCHEDULED', 'RESCHEDULED') " +
           "AND t.isDeleted = false ORDER BY t.scheduledDate ASC")
    List<Task> findUrgentTasks(@Param("organizationId") Long organizationId);

    // Find tasks for specific date (for daily scheduler view)
    @Query("SELECT t FROM Task t WHERE t.organization.id = :organizationId " +
           "AND t.scheduledDate = :date " +
           "AND t.isDeleted = false ORDER BY t.scheduledStartTime ASC")
    List<Task> findByOrganizationIdAndDate(@Param("organizationId") Long organizationId, 
                                            @Param("date") LocalDate date);
}
