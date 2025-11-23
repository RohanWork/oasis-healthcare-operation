package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.PlanOfCareIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanOfCareInterventionRepository extends JpaRepository<PlanOfCareIntervention, Long> {

    @Query("SELECT i FROM PlanOfCareIntervention i WHERE i.planOfCare.id = :pocId AND i.isDeleted = false")
    List<PlanOfCareIntervention> findByPlanOfCareId(@Param("pocId") Long pocId);

    @Query("SELECT i FROM PlanOfCareIntervention i WHERE i.planOfCare.id = :pocId AND i.responsibleDiscipline = :discipline AND i.isDeleted = false")
    List<PlanOfCareIntervention> findByPlanOfCareIdAndDiscipline(@Param("pocId") Long pocId, @Param("discipline") String discipline);
}

