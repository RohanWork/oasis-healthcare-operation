package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    @Query("SELECT i FROM Insurance i WHERE i.patient.id = :patientId AND i.isActive = true ORDER BY i.priorityOrder")
    List<Insurance> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT i FROM Insurance i WHERE i.patient.id = :patientId AND i.insuranceType = 'PRIMARY' AND i.isActive = true")
    Optional<Insurance> findPrimaryInsuranceByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT i FROM Insurance i WHERE i.patient.id = :patientId AND i.verificationStatus = :status")
    List<Insurance> findByPatientIdAndVerificationStatus(@Param("patientId") Long patientId, @Param("status") String status);

    @Query("SELECT i FROM Insurance i WHERE i.patient.organization.id = :organizationId AND i.verificationStatus = 'PENDING'")
    List<Insurance> findPendingVerificationsByOrganization(@Param("organizationId") Long organizationId);
}

