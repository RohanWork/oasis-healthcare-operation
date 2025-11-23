package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.auth.*;
import com.oasis.homehealth.security.UserPrincipal;
import com.oasis.homehealth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Authentication and Authorization APIs")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/select-organization")
    @Operation(summary = "Select organization", description = "Select organization after login and get updated token with permissions")
    public ResponseEntity<OrganizationSelectionResponse> selectOrganization(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody OrganizationSelectionRequest request) {
        OrganizationSelectionResponse response = authService.selectOrganization(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and clear security context")
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get currently authenticated user information")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", currentUser.getId());
        response.put("username", currentUser.getUsername());
        response.put("email", currentUser.getEmail());
        return ResponseEntity.ok(response);
    }
}

