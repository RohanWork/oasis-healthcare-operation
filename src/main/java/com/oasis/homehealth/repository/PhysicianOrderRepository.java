package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.PhysicianOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhysicianOrderRepository extends JpaRepository<PhysicianOrder, Long> {

    @Query("SELECT o FROM PhysicianOrder o WHERE o.planOfCare.id = :pocId AND o.isDeleted = false")
    List<PhysicianOrder> findByPlanOfCareId(@Param("pocId") Long pocId);

    @Query("SELECT o FROM PhysicianOrder o WHERE o.planOfCare.id = :pocId AND o.status = :status AND o.isDeleted = false")
    List<PhysicianOrder> findByPlanOfCareIdAndStatus(@Param("pocId") Long pocId, @Param("status") String status);

    @Query("SELECT o FROM PhysicianOrder o WHERE o.planOfCare.organization.id = :organizationId " +
           "AND o.signatureRequired = true AND (o.signatureObtained = false OR o.signatureObtained IS NULL) " +
           "AND o.isDeleted = false ORDER BY o.orderDate ASC")
    List<PhysicianOrder> findNeedingSignature(@Param("organizationId") Long organizationId);

    @Query("SELECT o FROM PhysicianOrder o WHERE o.planOfCare.organization.id = :organizationId " +
           "AND o.renewable = true AND o.renewalDate <= :currentDate " +
           "AND o.status = 'ACTIVE' AND o.isDeleted = false ORDER BY o.renewalDate ASC")
    List<PhysicianOrder> findNeedingRenewal(@Param("organizationId") Long organizationId, @Param("currentDate") LocalDate currentDate);
}

