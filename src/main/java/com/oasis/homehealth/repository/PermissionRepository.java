package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermissionName(String permissionName);

    List<Permission> findByModule(String module);

    @Query("SELECT p FROM Permission p WHERE p.isActive = true AND p.isDeleted = false ORDER BY p.module, p.action")
    List<Permission> findAllActivePermissions();
}

