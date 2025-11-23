package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    Optional<Referral> findByReferralNumber(String referralNumber);

    @Query("SELECT r FROM Referral r WHERE r.organization.id = :organizationId AND r.isActive = true AND r.isDeleted = false ORDER BY r.referralDate DESC, r.createdAt DESC")
    List<Referral> findByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT r FROM Referral r WHERE r.organization.id = :organizationId AND r.status = :status AND r.isActive = true AND r.isDeleted = false ORDER BY r.referralDate DESC")
    List<Referral> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, @Param("status") String status);

    @Query("SELECT r FROM Referral r WHERE r.organization.id = :organizationId AND " +
           "(LOWER(r.patientFirstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.patientLastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.referralSource) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.referralNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "r.isActive = true AND r.isDeleted = false ORDER BY r.referralDate DESC")
    List<Referral> searchReferrals(@Param("organizationId") Long organizationId, @Param("searchTerm") String searchTerm);
}

