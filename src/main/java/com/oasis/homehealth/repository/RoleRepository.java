package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    Boolean existsByRoleName(String roleName);

    @Query("SELECT r FROM Role r WHERE r.isActive = true AND r.isDeleted = false ORDER BY r.roleLevel")
    List<Role> findAllActiveRoles();

    List<Role> findByRoleLevelGreaterThanEqual(Integer roleLevel);
}

