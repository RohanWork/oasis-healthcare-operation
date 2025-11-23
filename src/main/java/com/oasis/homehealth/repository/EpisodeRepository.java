package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    Optional<Episode> findByEpisodeNumber(String episodeNumber);

    Boolean existsByEpisodeNumber(String episodeNumber);

    @Query("SELECT e FROM Episode e WHERE e.patient.id = :patientId AND e.isActive = true AND e.isDeleted = false ORDER BY e.startDate DESC")
    List<Episode> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT e FROM Episode e WHERE e.patient.id = :patientId AND e.status = 'ACTIVE' AND e.isActive = true AND e.isDeleted = false ORDER BY e.startDate DESC")
    Optional<Episode> findActiveEpisodeByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT e FROM Episode e WHERE e.organization.id = :organizationId AND e.isActive = true AND e.isDeleted = false ORDER BY e.startDate DESC")
    List<Episode> findByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT e FROM Episode e WHERE e.organization.id = :organizationId AND e.status = :status AND e.isActive = true AND e.isDeleted = false ORDER BY e.startDate DESC")
    List<Episode> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    @Query("SELECT e FROM Episode e WHERE e.caseManager.id = :userId AND e.status = 'ACTIVE' AND e.isActive = true AND e.isDeleted = false ORDER BY e.startDate DESC")
    List<Episode> findByCaseManagerId(@Param("userId") Long userId);

    @Query("SELECT e FROM Episode e WHERE e.primaryNurse.id = :userId AND e.status = 'ACTIVE' AND e.isActive = true AND e.isDeleted = false ORDER BY e.startDate DESC")
    List<Episode> findByPrimaryNurseId(@Param("userId") Long userId);

    @Query("SELECT e FROM Episode e WHERE e.organization.id = :organizationId AND e.status = 'ACTIVE' AND e.certificationEndDate <= :date AND e.isActive = true AND e.isDeleted = false ORDER BY e.certificationEndDate")
    List<Episode> findExpiringEpisodes(@Param("organizationId") Long organizationId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(e) FROM Episode e WHERE e.organization.id = :organizationId AND e.status = 'ACTIVE' AND e.isActive = true AND e.isDeleted = false")
    Long countActiveEpisodesByOrganization(@Param("organizationId") Long organizationId);

    @Query("SELECT COUNT(e) FROM Episode e WHERE e.organization.id = :organizationId AND e.status = 'PENDING' AND e.isActive = true AND e.isDeleted = false")
    Long countPendingEpisodesByOrganization(@Param("organizationId") Long organizationId);
}

