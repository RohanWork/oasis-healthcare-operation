package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.BillingClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingClaimRepository extends JpaRepository<BillingClaim, Long> {

    Optional<BillingClaim> findByClaimNumber(String claimNumber);

    @Query("SELECT b FROM BillingClaim b WHERE b.organization.id = :organizationId AND b.isDeleted = false ORDER BY b.billingDate DESC")
    List<BillingClaim> findByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT b FROM BillingClaim b WHERE b.patient.id = :patientId AND b.isDeleted = false ORDER BY b.billingDate DESC")
    List<BillingClaim> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT b FROM BillingClaim b WHERE b.episode.id = :episodeId AND b.isDeleted = false ORDER BY b.billingDate DESC")
    List<BillingClaim> findByEpisodeId(@Param("episodeId") Long episodeId);

    @Query("SELECT b FROM BillingClaim b WHERE b.organization.id = :organizationId AND b.status = :status AND b.isDeleted = false ORDER BY b.billingDate DESC")
    List<BillingClaim> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    @Query("SELECT b FROM BillingClaim b WHERE b.organization.id = :organizationId AND b.billingDate BETWEEN :startDate AND :endDate AND b.isDeleted = false ORDER BY b.billingDate DESC")
    List<BillingClaim> findByOrganizationIdAndDateRange(@Param("organizationId") Long organizationId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(b) FROM BillingClaim b WHERE b.organization.id = :organizationId AND b.status = :status AND b.isDeleted = false")
    Long countByStatus(@Param("organizationId") Long organizationId, @Param("status") String status);
}

