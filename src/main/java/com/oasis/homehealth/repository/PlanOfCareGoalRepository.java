package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.PlanOfCareGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanOfCareGoalRepository extends JpaRepository<PlanOfCareGoal, Long> {

    @Query("SELECT g FROM PlanOfCareGoal g WHERE g.planOfCare.id = :pocId AND g.isDeleted = false")
    List<PlanOfCareGoal> findByPlanOfCareId(@Param("pocId") Long pocId);

    @Query("SELECT g FROM PlanOfCareGoal g WHERE g.planOfCare.id = :pocId AND g.status = :status AND g.isDeleted = false")
    List<PlanOfCareGoal> findByPlanOfCareIdAndStatus(@Param("pocId") Long pocId, @Param("status") String status);
}

