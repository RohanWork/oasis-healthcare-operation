package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Find by user and date
    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId " +
           "AND s.scheduleDate = :date AND s.isDeleted = false")
    Optional<Schedule> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // Find by user and date range
    @Query("SELECT s FROM Schedule s WHERE s.user.id = :userId " +
           "AND s.scheduleDate BETWEEN :startDate AND :endDate " +
           "AND s.isDeleted = false ORDER BY s.scheduleDate ASC")
    List<Schedule> findByUserIdAndDateRange(@Param("userId") Long userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // Find by organization and date
    @Query("SELECT s FROM Schedule s WHERE s.organization.id = :organizationId " +
           "AND s.scheduleDate = :date AND s.isDeleted = false")
    List<Schedule> findByOrganizationIdAndDate(@Param("organizationId") Long organizationId,
                                                 @Param("date") LocalDate date);

    // Find available clinicians for date
    @Query("SELECT s FROM Schedule s WHERE s.organization.id = :organizationId " +
           "AND s.scheduleDate = :date " +
           "AND s.availability = 'AVAILABLE' " +
           "AND s.isDeleted = false")
    List<Schedule> findAvailableCliniciansByDate(@Param("organizationId") Long organizationId,
                                                   @Param("date") LocalDate date);

    // Find clinicians with capacity
    @Query("SELECT s FROM Schedule s WHERE s.organization.id = :organizationId " +
           "AND s.scheduleDate = :date " +
           "AND s.availability = 'AVAILABLE' " +
           "AND s.scheduledVisits < s.maxVisits " +
           "AND s.isDeleted = false")
    List<Schedule> findCliniciansWithCapacity(@Param("organizationId") Long organizationId,
                                                @Param("date") LocalDate date);
}

