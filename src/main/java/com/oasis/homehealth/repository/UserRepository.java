package com.oasis.homehealth.repository;

import com.oasis.homehealth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.organizations o WHERE o.id = :organizationId AND u.isActive = true AND u.isDeleted = false")
    List<User> findByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName AND u.isActive = true AND u.isDeleted = false")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.isDeleted = false")
    List<User> findAllActiveUsers();
}

