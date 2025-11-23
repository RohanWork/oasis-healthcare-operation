package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.PlanOfCareFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanOfCareFrequencyRepository extends JpaRepository<PlanOfCareFrequency, Long> {

    @Query("SELECT f FROM PlanOfCareFrequency f WHERE f.planOfCare.id = :pocId AND f.isDeleted = false")
    List<PlanOfCareFrequency> findByPlanOfCareId(@Param("pocId") Long pocId);

    @Query("SELECT f FROM PlanOfCareFrequency f WHERE f.planOfCare.id = :pocId AND f.disciplineType = :discipline AND f.isDeleted = false")
    List<PlanOfCareFrequency> findByPlanOfCareIdAndDiscipline(@Param("pocId") Long pocId, @Param("discipline") String discipline);
}

