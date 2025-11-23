package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByOrganizationCode(String organizationCode);

    Boolean existsByOrganizationCode(String organizationCode);

    @Query("SELECT o FROM Organization o WHERE o.isActive = true AND o.isDeleted = false AND o.isTenantActive = true")
    List<Organization> findAllActiveOrganizations();

    @Query("SELECT o FROM Organization o WHERE o.parentOrganization.id = :parentId AND o.isActive = true")
    List<Organization> findByParentOrganizationId(@Param("parentId") Long parentId);

    @Query("SELECT o FROM Organization o JOIN o.users u WHERE u.id = :userId AND o.isActive = true AND o.isTenantActive = true")
    List<Organization> findByUserId(@Param("userId") Long userId);

    List<Organization> findByOrganizationType(String organizationType);
}

