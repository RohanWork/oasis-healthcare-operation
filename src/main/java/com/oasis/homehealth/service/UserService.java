package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.RoleDTO;
import com.oasis.homehealth.dto.UserDTO;
import com.oasis.homehealth.dto.UserRequest;
import com.oasis.homehealth.entity.Organization;
import com.oasis.homehealth.entity.Role;
import com.oasis.homehealth.entity.User;
import com.oasis.homehealth.repository.OrganizationRepository;
import com.oasis.homehealth.repository.RoleRepository;
import com.oasis.homehealth.repository.UserRepository;
import com.oasis.homehealth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     * ORG_ADMIN can create users in their organization
     * SYSTEM_ADMIN can create users in any organization
     */
    public UserDTO createUser(UserRequest request, Long organizationId) {
        log.info("Creating user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Validate password is provided for new users
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required for new users");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .phoneNumber(request.getPhoneNumber())
                .mobileNumber(request.getMobileNumber())
                .dateOfBirth(request.getDateOfBirth())
                .licenseNumber(request.getLicenseNumber())
                .licenseState(request.getLicenseState())
                .licenseExpiry(request.getLicenseExpiry())
                .npiNumber(request.getNpiNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .isEmailVerified(request.getIsEmailVerified() != null ? request.getIsEmailVerified() : false)
                .isLocked(request.getIsLocked() != null ? request.getIsLocked() : false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();

        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        user.setIsDeleted(false);

        // Assign roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        // Assign organizations
        if (request.getOrganizationIds() != null && !request.getOrganizationIds().isEmpty()) {
            Set<Organization> organizations = request.getOrganizationIds().stream()
                    .map(orgId -> organizationRepository.findById(orgId)
                            .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId)))
                    .collect(Collectors.toSet());
            user.setOrganizations(organizations);
        } else if (organizationId != null) {
            // If no organizations specified but organizationId provided, assign to that organization
            Organization organization = organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new RuntimeException("Organization not found: " + organizationId));
            user.getOrganizations().add(organization);
        }

        User saved = userRepository.save(user);
        log.info("User created: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Update an existing user
     * ORG_ADMIN can update users in their organization (but not SYSTEM_ADMIN users)
     * SYSTEM_ADMIN can update any user
     */
    public UserDTO updateUser(Long id, UserRequest request, Long organizationId) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is deleted
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new RuntimeException("User not found");
        }

        // Authorization check: ORG_ADMIN can only update users in their organization
        UserPrincipal currentUser = getCurrentUser();
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        if (!isSystemAdmin) {
            // Check if the user being edited is a SYSTEM_ADMIN (ORG_ADMIN cannot edit SYSTEM_ADMIN)
            boolean targetIsSystemAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getRoleName().equals("ROLE_SYSTEM_ADMIN"));
            
            if (targetIsSystemAdmin) {
                throw new RuntimeException("You do not have permission to edit System Administrator users");
            }

            // Check if the user belongs to the ORG_ADMIN's organization
            if (organizationId == null) {
                throw new RuntimeException("Organization ID is required");
            }

            boolean userBelongsToOrg = user.getOrganizations().stream()
                    .anyMatch(org -> org.getId().equals(organizationId));
            
            if (!userBelongsToOrg) {
                throw new RuntimeException("You do not have permission to edit users outside your organization");
            }
        }

        // Check if username is being changed and if new username already exists
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setMobileNumber(request.getMobileNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setLicenseNumber(request.getLicenseNumber());
        user.setLicenseState(request.getLicenseState());
        user.setLicenseExpiry(request.getLicenseExpiry());
        user.setNpiNumber(request.getNpiNumber());
        user.setAddressLine1(request.getAddressLine1());
        user.setAddressLine2(request.getAddressLine2());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZipCode(request.getZipCode());

        // Update password only if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getIsEmailVerified() != null) {
            user.setIsEmailVerified(request.getIsEmailVerified());
        }
        if (request.getIsLocked() != null) {
            user.setIsLocked(request.getIsLocked());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        // Update roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = request.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        // Update organizations
        if (request.getOrganizationIds() != null) {
            Set<Organization> organizations = request.getOrganizationIds().stream()
                    .map(orgId -> organizationRepository.findById(orgId)
                            .orElseThrow(() -> new RuntimeException("Organization not found: " + orgId)))
                    .collect(Collectors.toSet());
            user.setOrganizations(organizations);
        }

        User saved = userRepository.save(user);
        log.info("User updated: {}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * Get all users (filtered by organization for ORG_ADMIN)
     * ORG_ADMIN cannot see SYSTEM_ADMIN users
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(Long organizationId) {
        UserPrincipal currentUser = getCurrentUser();
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        List<User> users;
        if (isSystemAdmin) {
            // SYSTEM_ADMIN can see all users
            users = userRepository.findAllActiveUsers();
            log.debug("SYSTEM_ADMIN requesting all users. Found {} users", users.size());
        } else if (organizationId != null) {
            // ORG_ADMIN can see users in their organization, but NOT SYSTEM_ADMIN users
            users = userRepository.findByOrganizationId(organizationId);
            log.debug("ORG_ADMIN requesting users for organization {}. Found {} users before filtering", organizationId, users.size());
            
            // Filter out SYSTEM_ADMIN users
            int beforeFilter = users.size();
            users = users.stream()
                    .filter(user -> {
                        boolean hasSystemAdminRole = user.getRoles().stream()
                                .anyMatch(role -> role.getRoleName().equals("ROLE_SYSTEM_ADMIN"));
                        if (hasSystemAdminRole) {
                            log.debug("Filtering out SYSTEM_ADMIN user: {} (ID: {})", user.getUsername(), user.getId());
                        }
                        return !hasSystemAdminRole;
                    })
                    .collect(Collectors.toList());
            log.debug("After filtering SYSTEM_ADMIN users: {} users (filtered out {})", users.size(), beforeFilter - users.size());
        } else {
            throw new RuntimeException("Organization ID is required for non-system administrators");
        }

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     * ORG_ADMIN can only view users in their organization (but not SYSTEM_ADMIN users)
     * SYSTEM_ADMIN can view any user
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id, Long organizationId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new RuntimeException("User not found");
        }

        // Authorization check: ORG_ADMIN can only view users in their organization
        UserPrincipal currentUser = getCurrentUser();
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        if (!isSystemAdmin) {
            // Check if the user being viewed is a SYSTEM_ADMIN (ORG_ADMIN cannot view SYSTEM_ADMIN)
            boolean targetIsSystemAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getRoleName().equals("ROLE_SYSTEM_ADMIN"));
            
            if (targetIsSystemAdmin) {
                throw new RuntimeException("You do not have permission to view System Administrator users");
            }

            // Check if the user belongs to the ORG_ADMIN's organization
            if (organizationId == null) {
                throw new RuntimeException("Organization ID is required");
            }

            boolean userBelongsToOrg = user.getOrganizations().stream()
                    .anyMatch(org -> org.getId().equals(organizationId));
            
            if (!userBelongsToOrg) {
                throw new RuntimeException("You do not have permission to view users outside your organization");
            }
        }

        return convertToDTO(user);
    }

    /**
     * Soft delete user
     * ORG_ADMIN can only delete users in their organization (but not SYSTEM_ADMIN users)
     * SYSTEM_ADMIN can delete any user
     */
    public void deleteUser(Long id, Long organizationId) {
        log.info("Deleting user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new RuntimeException("User not found");
        }

        // Authorization check: ORG_ADMIN can only delete users in their organization
        UserPrincipal currentUser = getCurrentUser();
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        if (!isSystemAdmin) {
            // Check if the user being deleted is a SYSTEM_ADMIN (ORG_ADMIN cannot delete SYSTEM_ADMIN)
            boolean targetIsSystemAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getRoleName().equals("ROLE_SYSTEM_ADMIN"));
            
            if (targetIsSystemAdmin) {
                throw new RuntimeException("You do not have permission to delete System Administrator users");
            }

            // Check if the user belongs to the ORG_ADMIN's organization
            if (organizationId == null) {
                throw new RuntimeException("Organization ID is required");
            }

            boolean userBelongsToOrg = user.getOrganizations().stream()
                    .anyMatch(org -> org.getId().equals(organizationId));
            
            if (!userBelongsToOrg) {
                throw new RuntimeException("You do not have permission to delete users outside your organization");
            }
        }

        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);

        log.info("User deleted: {}", id);
    }

    /**
     * Assign user to organization
     * ORG_ADMIN can only assign users in their organization (but not SYSTEM_ADMIN users)
     * SYSTEM_ADMIN can assign any user to any organization
     */
    public UserDTO assignOrganization(Long userId, Long organizationId, Long requesterOrganizationId) {
        log.info("Assigning user {} to organization {}", userId, organizationId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new RuntimeException("User not found");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Authorization check: ORG_ADMIN can only assign users in their organization
        UserPrincipal currentUser = getCurrentUser();
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        if (!isSystemAdmin) {
            // Check if the user being assigned is a SYSTEM_ADMIN (ORG_ADMIN cannot assign SYSTEM_ADMIN)
            boolean targetIsSystemAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getRoleName().equals("ROLE_SYSTEM_ADMIN"));
            
            if (targetIsSystemAdmin) {
                throw new RuntimeException("You do not have permission to assign System Administrator users");
            }

            // ORG_ADMIN can only assign users that belong to their organization
            if (requesterOrganizationId == null) {
                throw new RuntimeException("Organization ID is required");
            }

            boolean userBelongsToOrg = user.getOrganizations().stream()
                    .anyMatch(org -> org.getId().equals(requesterOrganizationId));
            
            if (!userBelongsToOrg) {
                throw new RuntimeException("You do not have permission to assign users outside your organization");
            }

            // ORG_ADMIN can only assign to their own organization
            if (!organizationId.equals(requesterOrganizationId)) {
                throw new RuntimeException("You can only assign users to your own organization");
            }
        }

        user.getOrganizations().add(organization);
        User saved = userRepository.save(user);

        return convertToDTO(saved);
    }

    /**
     * Assign role to user
     * ORG_ADMIN can only assign roles to users in their organization (but not SYSTEM_ADMIN role)
     * SYSTEM_ADMIN can assign any role to any user
     */
    public UserDTO assignRole(Long userId, Long roleId, Long organizationId) {
        log.info("Assigning role {} to user {}", roleId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new RuntimeException("User not found");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Authorization check: ORG_ADMIN restrictions
        UserPrincipal currentUser = getCurrentUser();
        boolean isSystemAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));

        if (!isSystemAdmin) {
            // ORG_ADMIN cannot assign SYSTEM_ADMIN role
            if (role.getRoleName().equals("ROLE_SYSTEM_ADMIN")) {
                throw new RuntimeException("You do not have permission to assign System Administrator role");
            }

            // Check if the user being assigned is a SYSTEM_ADMIN (ORG_ADMIN cannot modify SYSTEM_ADMIN)
            boolean targetIsSystemAdmin = user.getRoles().stream()
                    .anyMatch(r -> r.getRoleName().equals("ROLE_SYSTEM_ADMIN"));
            
            if (targetIsSystemAdmin) {
                throw new RuntimeException("You do not have permission to modify System Administrator users");
            }

            // Check if the user belongs to the ORG_ADMIN's organization
            if (organizationId == null) {
                throw new RuntimeException("Organization ID is required");
            }

            boolean userBelongsToOrg = user.getOrganizations().stream()
                    .anyMatch(org -> org.getId().equals(organizationId));
            
            if (!userBelongsToOrg) {
                throw new RuntimeException("You do not have permission to assign roles to users outside your organization");
            }
        }

        user.getRoles().add(role);
        User saved = userRepository.save(user);

        return convertToDTO(saved);
    }

    /**
     * Convert User entity to DTO
     */
    private UserDTO convertToDTO(User user) {
        List<RoleDTO> roleDTOs = user.getRoles().stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .roleName(role.getRoleName())
                        .displayName(role.getDisplayName())
                        .description(role.getDescription())
                        .roleLevel(role.getRoleLevel())
                        .build())
                .collect(Collectors.toList());

        List<com.oasis.homehealth.dto.OrganizationDTO> orgDTOs = user.getOrganizations().stream()
                .map(org -> com.oasis.homehealth.dto.OrganizationDTO.builder()
                        .id(org.getId())
                        .organizationCode(org.getOrganizationCode())
                        .organizationName(org.getOrganizationName())
                        .build())
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .mobileNumber(user.getMobileNumber())
                .dateOfBirth(user.getDateOfBirth())
                .licenseNumber(user.getLicenseNumber())
                .licenseState(user.getLicenseState())
                .licenseExpiry(user.getLicenseExpiry())
                .npiNumber(user.getNpiNumber())
                .addressLine1(user.getAddressLine1())
                .addressLine2(user.getAddressLine2())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .isEmailVerified(user.getIsEmailVerified())
                .isLocked(user.getIsLocked())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .roles(roleDTOs)
                .organizations(orgDTOs)
                .build();
    }

    /**
     * Get clinicians (RN, PT, OT, ST) by organization
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getCliniciansByOrganization(Long organizationId) {
        List<User> users = userRepository.findByOrganizationId(organizationId);
        
        // Filter to only clinicians (RN, PT, OT, ST)
        List<User> clinicians = users.stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> {
                            String roleName = role.getRoleName();
                            return "RN".equals(roleName) || 
                                   "PT".equals(roleName) || 
                                   "OT".equals(roleName) || 
                                   "ST".equals(roleName);
                        }))
                .collect(Collectors.toList());
        
        return clinicians.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get current user from security context
     */
    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

