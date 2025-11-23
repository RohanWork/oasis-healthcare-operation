package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.UserDTO;
import com.oasis.homehealth.dto.UserRequest;
import com.oasis.homehealth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER_CREATE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user (ORG_ADMIN for their org, SYSTEM_ADMIN for any org)")
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody UserRequest request,
            HttpServletRequest httpRequest) {
        log.info("REST request to create user: {}", request.getUsername());
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        UserDTO user = userService.createUser(request, organizationId);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER_UPDATE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Update user", description = "Update an existing user (ORG_ADMIN for their org, SYSTEM_ADMIN for any org)")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        log.info("REST request to update user: {}", id);
        UserDTO user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER_READ') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Get all users", description = "Get all users (filtered by organization for ORG_ADMIN)")
    public ResponseEntity<List<UserDTO>> getAllUsers(HttpServletRequest httpRequest) {
        log.info("REST request to get all users");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<UserDTO> users = userService.getAllUsers(organizationId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER_READ') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Get user by ID", description = "Get user details")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("REST request to get user: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER_DELETE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Delete user", description = "Soft delete a user")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user: {}", id);
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/assign-organization")
    @PreAuthorize("hasAnyAuthority('USER_UPDATE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Assign user to organization", description = "Assign a user to an organization")
    public ResponseEntity<UserDTO> assignOrganization(
            @PathVariable Long id,
            @RequestParam Long organizationId) {
        log.info("REST request to assign user {} to organization {}", id, organizationId);
        UserDTO user = userService.assignOrganization(id, organizationId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/assign-role")
    @PreAuthorize("hasAnyAuthority('USER_UPDATE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Assign role to user", description = "Assign a role to a user")
    public ResponseEntity<UserDTO> assignRole(
            @PathVariable Long id,
            @RequestParam Long roleId) {
        log.info("REST request to assign role {} to user {}", roleId, id);
        UserDTO user = userService.assignRole(id, roleId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/clinicians")
    @PreAuthorize("hasAnyAuthority('USER_READ', 'PATIENT_CREATE', 'PATIENT_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_INTAKE_COORDINATOR')")
    @Operation(summary = "Get clinicians by organization", description = "Get all clinicians (RN, PT, OT, ST) in the organization")
    public ResponseEntity<List<UserDTO>> getClinicians(HttpServletRequest httpRequest) {
        log.info("REST request to get clinicians");
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<UserDTO> clinicians = userService.getCliniciansByOrganization(organizationId);
        return ResponseEntity.ok(clinicians);
    }
}

