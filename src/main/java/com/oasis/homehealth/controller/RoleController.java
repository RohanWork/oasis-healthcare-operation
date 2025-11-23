package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.RoleDTO;
import com.oasis.homehealth.repository.RoleRepository;
import com.oasis.homehealth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Role Management", description = "APIs for managing roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER_READ', 'USER_CREATE', 'USER_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get all roles", description = "Get all active roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        log.info("REST request to get all roles");
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get role by ID", description = "Get role details")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        log.info("REST request to get role: {}", id);
        RoleDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }
}

