package com.oasis.homehealth.service;

import com.oasis.homehealth.entity.Organization;
import com.oasis.homehealth.entity.Permission;
import com.oasis.homehealth.entity.Role;
import com.oasis.homehealth.entity.User;
import com.oasis.homehealth.repository.OrganizationRepository;
import com.oasis.homehealth.repository.PermissionRepository;
import com.oasis.homehealth.repository.RoleRepository;
import com.oasis.homehealth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class DataInitializationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");

        initializePermissions();
        initializeRoles();
        initializeOrganizations();
        initializeUsers();

        logger.info("Data initialization completed successfully!");
    }

    private void initializePermissions() {
        if (permissionRepository.count() > 0) {
            logger.info("Permissions already exist, skipping initialization");
            return;
        }

        logger.info("Initializing permissions...");

        List<Permission> permissions = Arrays.asList(
            // User Management
            createPermission("USER_CREATE", "Create User", "Create new users", "USER", "CREATE"),
            createPermission("USER_READ", "View User", "View user details", "USER", "READ"),
            createPermission("USER_UPDATE", "Update User", "Update user information", "USER", "UPDATE"),
            createPermission("USER_DELETE", "Delete User", "Delete users", "USER", "DELETE"),

            // Patient Management
            createPermission("PATIENT_CREATE", "Create Patient", "Create new patients", "PATIENT", "CREATE"),
            createPermission("PATIENT_READ", "View Patient", "View patient details", "PATIENT", "READ"),
            createPermission("PATIENT_UPDATE", "Update Patient", "Update patient information", "PATIENT", "UPDATE"),
            createPermission("PATIENT_DELETE", "Delete Patient", "Delete patients", "PATIENT", "DELETE"),

            // Episode Management
            createPermission("EPISODE_CREATE", "Create Episode", "Create new episodes", "EPISODE", "CREATE"),
            createPermission("EPISODE_READ", "View Episode", "View episode details", "EPISODE", "READ"),
            createPermission("EPISODE_UPDATE", "Update Episode", "Update episode information", "EPISODE", "UPDATE"),
            createPermission("EPISODE_DELETE", "Delete Episode", "Delete episodes", "EPISODE", "DELETE"),

            // OASIS Management
            createPermission("OASIS_CREATE", "Create OASIS", "Create OASIS assessments", "OASIS", "CREATE"),
            createPermission("OASIS_READ", "View OASIS", "View OASIS assessments", "OASIS", "READ"),
            createPermission("OASIS_UPDATE", "Update OASIS", "Update OASIS assessments", "OASIS", "UPDATE"),
            createPermission("OASIS_DELETE", "Delete OASIS", "Delete OASIS assessments", "OASIS", "DELETE"),
            createPermission("OASIS_SUBMIT", "Submit OASIS", "Submit OASIS for QA", "OASIS", "SUBMIT"),
            createPermission("OASIS_APPROVE", "Approve OASIS", "Approve OASIS assessments", "OASIS", "APPROVE"),

            // Plan of Care Management
            createPermission("POC_CREATE", "Create POC", "Create Plan of Care", "POC", "CREATE"),
            createPermission("POC_READ", "View POC", "View Plan of Care", "POC", "READ"),
            createPermission("POC_UPDATE", "Update POC", "Update Plan of Care", "POC", "UPDATE"),
            createPermission("POC_DELETE", "Delete POC", "Delete Plan of Care", "POC", "DELETE"),
            createPermission("POC_GENERATE", "Generate POC", "Generate POC from OASIS", "POC", "GENERATE"),
            createPermission("POC_APPROVE", "Approve POC", "Approve Plan of Care", "POC", "APPROVE"),

            // Task/Scheduling Management
            createPermission("TASK_CREATE", "Create Task", "Create tasks and visits", "TASK", "CREATE"),
            createPermission("TASK_READ", "View Task", "View task details", "TASK", "READ"),
            createPermission("TASK_UPDATE", "Update Task", "Update task information", "TASK", "UPDATE"),
            createPermission("TASK_DELETE", "Delete Task", "Delete tasks", "TASK", "DELETE"),
            createPermission("TASK_GENERATE", "Generate Tasks", "Auto-generate tasks from POC", "TASK", "GENERATE"),
            createPermission("TASK_ASSIGN", "Assign Task", "Assign tasks to clinicians", "TASK", "ASSIGN"),
            createPermission("TASK_RESCHEDULE", "Reschedule Task", "Reschedule tasks", "TASK", "RESCHEDULE"),
            createPermission("TASK_COMPLETE", "Complete Task", "Mark tasks as completed", "TASK", "COMPLETE"),
            createPermission("TASK_CANCEL", "Cancel Task", "Cancel tasks", "TASK", "CANCEL"),

            // Visit Management
            createPermission("VISIT_CREATE", "Create Visit", "Create visits", "VISIT", "CREATE"),
            createPermission("VISIT_READ", "View Visit", "View visit details", "VISIT", "READ"),
            createPermission("VISIT_UPDATE", "Update Visit", "Update visit information", "VISIT", "UPDATE"),
            createPermission("VISIT_DELETE", "Delete Visit", "Delete visits", "VISIT", "DELETE"),
            createPermission("VISIT_APPROVE", "Approve Visit", "Approve visit notes after QA review", "VISIT", "APPROVE"),

            // Billing Management
            createPermission("BILLING_CREATE", "Create Billing", "Create billing records", "BILLING", "CREATE"),
            createPermission("BILLING_READ", "View Billing", "View billing records", "BILLING", "READ"),
            createPermission("BILLING_UPDATE", "Update Billing", "Update billing records", "BILLING", "UPDATE"),
            createPermission("BILLING_APPROVE", "Approve Billing", "Approve billing claims", "BILLING", "APPROVE"),

            // Organization Management
            createPermission("ORG_CREATE", "Create Organization", "Create organizations", "ORGANIZATION", "CREATE"),
            createPermission("ORG_READ", "View Organization", "View organization details", "ORGANIZATION", "READ"),
            createPermission("ORG_UPDATE", "Update Organization", "Update organization information", "ORGANIZATION", "UPDATE"),
            createPermission("ORG_DELETE", "Delete Organization", "Delete organizations", "ORGANIZATION", "DELETE"),

            // Reports
            createPermission("REPORT_VIEW", "View Reports", "View all reports", "REPORT", "READ"),
            createPermission("REPORT_EXPORT", "Export Reports", "Export reports", "REPORT", "EXPORT")
        );

        permissionRepository.saveAll(permissions);
        logger.info("Initialized {} permissions", permissions.size());
    }

    private Permission createPermission(String name, String displayName, String description, String module, String action) {
        Permission permission = Permission.builder()
                .permissionName(name)
                .displayName(displayName)
                .description(description)
                .module(module)
                .action(action)
                .build();
        permission.setIsActive(true);
        permission.setIsDeleted(false);
        return permission;
    }

    private void initializeRoles() {
        if (roleRepository.count() > 0) {
            logger.info("Roles already exist, skipping initialization");
            return;
        }

        logger.info("Initializing roles...");

        // System Admin - Full access
        Role systemAdmin = createRole("SYSTEM_ADMIN", "System Administrator", 
                "Full system access", 1);
        systemAdmin.setPermissions(new HashSet<>(permissionRepository.findAll()));

        // Organization Admin - Limited to organization-level permissions only
        // NOTE: ORG_ADMIN does NOT have:
        // - Organization creation/deletion (ORG_CREATE, ORG_UPDATE, ORG_DELETE) - SYSTEM_ADMIN only
        // - System-wide settings access
        // - Cross-organization data access (filtered by organization)
        Role orgAdmin = createRole("ORG_ADMIN", "Organization Administrator", 
                "Organization level administrator", 2);
        addPermissionsToRole(orgAdmin, Arrays.asList(
                // User Management (within organization only)
                "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
                // Patient Management
                "PATIENT_CREATE", "PATIENT_READ", "PATIENT_UPDATE", "PATIENT_DELETE",
                // Episode Management
                "EPISODE_CREATE", "EPISODE_READ", "EPISODE_UPDATE", "EPISODE_DELETE",
                // OASIS Management
                "OASIS_CREATE", "OASIS_READ", "OASIS_UPDATE", "OASIS_SUBMIT", "OASIS_APPROVE", "OASIS_DELETE",
                // Plan of Care Management
                "POC_CREATE", "POC_READ", "POC_UPDATE", "POC_GENERATE", "POC_APPROVE", "POC_DELETE",
                // Task Management
                "TASK_CREATE", "TASK_READ", "TASK_UPDATE", "TASK_DELETE", "TASK_GENERATE", 
                "TASK_ASSIGN", "TASK_RESCHEDULE", "TASK_COMPLETE", "TASK_CANCEL",
                // Visit Management
                "VISIT_CREATE", "VISIT_READ", "VISIT_UPDATE", "VISIT_DELETE", "VISIT_APPROVE",
                // Billing Management
                "BILLING_CREATE", "BILLING_READ", "BILLING_UPDATE", "BILLING_DELETE",
                // Reports
                "REPORT_VIEW"
                // Explicitly EXCLUDED: ORG_CREATE, ORG_UPDATE, ORG_DELETE (SYSTEM_ADMIN only)
        ));

        // Clinical Manager
        Role clinicalManager = createRole("CLINICAL_MANAGER", "Clinical Manager", 
                "Manages clinical staff and QA", 3);
        addPermissionsToRole(clinicalManager, Arrays.asList(
                "PATIENT_READ", "PATIENT_UPDATE",
                "EPISODE_READ", "EPISODE_UPDATE",
                "OASIS_READ", "OASIS_APPROVE",
                "POC_READ", "POC_APPROVE",
                "TASK_CREATE", "TASK_READ", "TASK_UPDATE", "TASK_DELETE", "TASK_GENERATE", "TASK_ASSIGN", "TASK_RESCHEDULE",
                "VISIT_READ", "VISIT_UPDATE", "VISIT_APPROVE",
                "USER_READ", "REPORT_VIEW"
        ));

        // QA Nurse (Supervisor of RN, reviews and approves clinical documentation)
        Role qaNurse = createRole("QA_NURSE", "QA Nurse", 
                "Supervises RN, reviews and approves clinical documentation, can add patients and start OASIS", 3);
        addPermissionsToRole(qaNurse, Arrays.asList(
                // Patient Management
                "PATIENT_CREATE", "PATIENT_READ", "PATIENT_UPDATE",
                // Episode Management
                "EPISODE_CREATE", "EPISODE_READ", "EPISODE_UPDATE",
                // OASIS Management
                "OASIS_CREATE", "OASIS_READ", "OASIS_UPDATE", "OASIS_SUBMIT", "OASIS_APPROVE",
                // Plan of Care
                "POC_CREATE", "POC_READ", "POC_UPDATE", "POC_GENERATE", "POC_APPROVE",
                // Visit Notes
                "VISIT_READ", "VISIT_UPDATE", "VISIT_APPROVE",
                // Reports
                "REPORT_VIEW"
        ));

        // Intake Coordinator
        Role intakeCoordinator = createRole("INTAKE_COORDINATOR", "Intake Coordinator", 
                "Handles patient intake and referrals", 4);
        addPermissionsToRole(intakeCoordinator, Arrays.asList(
                "PATIENT_CREATE", "PATIENT_READ", "PATIENT_UPDATE",
                "EPISODE_CREATE", "EPISODE_READ", "EPISODE_UPDATE",
                // OASIS Management - Intake coordinators can create and update OASIS during intake
                "OASIS_CREATE", "OASIS_READ", "OASIS_UPDATE", "OASIS_SUBMIT"
        ));

        // Registered Nurse (RN)
        Role rn = createRole("RN", "Registered Nurse", 
                "Provides skilled nursing care", 5);
        addPermissionsToRole(rn, Arrays.asList(
                "PATIENT_READ",
                "EPISODE_READ", "EPISODE_UPDATE",
                "OASIS_CREATE", "OASIS_READ", "OASIS_UPDATE", "OASIS_SUBMIT",
                "POC_CREATE", "POC_READ", "POC_UPDATE", "POC_GENERATE",
                "TASK_READ", "TASK_UPDATE", "TASK_COMPLETE",
                "VISIT_CREATE", "VISIT_READ", "VISIT_UPDATE"
        ));

        // Physical Therapist (PT)
        Role pt = createRole("PT", "Physical Therapist", 
                "Provides physical therapy services", 5);
        addPermissionsToRole(pt, Arrays.asList(
                "PATIENT_READ",
                "EPISODE_READ",
                "OASIS_CREATE", "OASIS_READ", "OASIS_UPDATE", "OASIS_SUBMIT",
                "POC_CREATE", "POC_READ", "POC_UPDATE", "POC_GENERATE",
                "TASK_READ", "TASK_UPDATE", "TASK_COMPLETE",
                "VISIT_CREATE", "VISIT_READ", "VISIT_UPDATE"
        ));

        // Occupational Therapist (OT)
        Role ot = createRole("OT", "Occupational Therapist", 
                "Provides occupational therapy services", 5);
        addPermissionsToRole(ot, Arrays.asList(
                "PATIENT_READ",
                "EPISODE_READ",
                "TASK_READ", "TASK_UPDATE", "TASK_COMPLETE",
                "VISIT_CREATE", "VISIT_READ", "VISIT_UPDATE"
        ));

        // Home Health Aide (HHA)
        Role hha = createRole("HHA", "Home Health Aide", 
                "Provides personal care services", 6);
        addPermissionsToRole(hha, Arrays.asList(
                "PATIENT_READ",
                "TASK_READ", "TASK_COMPLETE",
                "VISIT_READ", "VISIT_UPDATE"
        ));

        // Billing Specialist
        Role billingSpecialist = createRole("BILLING_SPECIALIST", "Billing Specialist", 
                "Handles billing and claims", 4);
        addPermissionsToRole(billingSpecialist, Arrays.asList(
                "PATIENT_READ",
                "EPISODE_READ",
                "VISIT_READ",
                "BILLING_CREATE", "BILLING_READ", "BILLING_UPDATE", "BILLING_DELETE"
        ));

        // Scheduler
        Role scheduler = createRole("SCHEDULER", "Scheduler", 
                "Manages visit scheduling", 4);
        addPermissionsToRole(scheduler, Arrays.asList(
                "PATIENT_READ",
                "EPISODE_READ",
                "TASK_CREATE", "TASK_READ", "TASK_UPDATE", "TASK_ASSIGN", "TASK_RESCHEDULE", "TASK_CANCEL",
                "VISIT_CREATE", "VISIT_READ", "VISIT_UPDATE",
                "USER_READ"
        ));

        roleRepository.saveAll(Arrays.asList(
                systemAdmin, orgAdmin, clinicalManager, qaNurse, intakeCoordinator,
                rn, pt, ot, hha, billingSpecialist, scheduler
        ));

        logger.info("Initialized 11 roles");
    }

    private Role createRole(String name, String displayName, String description, int level) {
        Role role = Role.builder()
                .roleName(name)
                .displayName(displayName)
                .description(description)
                .roleLevel(level)
                .permissions(new HashSet<>())
                .build();
        role.setIsActive(true);
        role.setIsDeleted(false);
        return role;
    }

    private void addPermissionsToRole(Role role, List<String> permissionNames) {
        permissionNames.forEach(permName -> {
            permissionRepository.findByPermissionName(permName).ifPresent(perm -> {
                role.getPermissions().add(perm);
            });
        });
    }

    private void initializeOrganizations() {
        if (organizationRepository.count() > 0) {
            logger.info("Organizations already exist, skipping initialization");
            return;
        }

        logger.info("Initializing organizations...");

        Organization corporate = Organization.builder()
                .organizationCode("OASIS-CORP")
                .organizationName("OASIS Home Health Corporate")
                .legalName("OASIS Home Health Services Inc.")
                .taxId("12-3456789")
                .npiNumber("1234567890")
                .phoneNumber("555-0100")
                .email("corporate@oasishomehealth.com")
                .addressLine1("123 Healthcare Blvd")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .country("USA")
                .organizationType("CORPORATE")
                .subscriptionTier("ENTERPRISE")
                .maxUsers(500)
                .maxPatients(10000)
                .isTenantActive(true)
                .build();
        corporate.setIsActive(true);
        corporate.setIsDeleted(false);

        organizationRepository.save(corporate);

        Organization branch1 = Organization.builder()
                .organizationCode("OASIS-NYC-01")
                .organizationName("OASIS Home Health - NYC Branch 1")
                .legalName("OASIS Home Health Services Inc.")
                .taxId("12-3456789")
                .npiNumber("1234567891")
                .phoneNumber("555-0101")
                .email("nyc1@oasishomehealth.com")
                .addressLine1("456 Manhattan Ave")
                .city("New York")
                .state("NY")
                .zipCode("10002")
                .country("USA")
                .organizationType("BRANCH")
                .subscriptionTier("PROFESSIONAL")
                .maxUsers(50)
                .maxPatients(1000)
                .isTenantActive(true)
                .parentOrganization(corporate)
                .build();
        branch1.setIsActive(true);
        branch1.setIsDeleted(false);

        Organization branch2 = Organization.builder()
                .organizationCode("OASIS-LA-01")
                .organizationName("OASIS Home Health - LA Branch 1")
                .legalName("OASIS Home Health Services Inc.")
                .taxId("12-3456789")
                .npiNumber("1234567892")
                .phoneNumber("555-0102")
                .email("la1@oasishomehealth.com")
                .addressLine1("789 Sunset Blvd")
                .city("Los Angeles")
                .state("CA")
                .zipCode("90001")
                .country("USA")
                .organizationType("BRANCH")
                .subscriptionTier("PROFESSIONAL")
                .maxUsers(50)
                .maxPatients(1000)
                .isTenantActive(true)
                .parentOrganization(corporate)
                .build();
        branch2.setIsActive(true);
        branch2.setIsDeleted(false);

        organizationRepository.saveAll(Arrays.asList(branch1, branch2));

        logger.info("Initialized 3 organizations");
    }

    private void initializeUsers() {
        if (userRepository.count() > 0) {
            logger.info("Users already exist, skipping initialization");
            return;
        }

        logger.info("Initializing users...");

        // System Admin User
        User admin = User.builder()
                .username("admin")
                .email("admin@oasishomehealth.com")
                .password(passwordEncoder.encode("Admin@123"))
                .firstName("System")
                .lastName("Administrator")
                .phoneNumber("555-0100")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        admin.setIsActive(true);
        admin.setIsDeleted(false);

        Role adminRole = roleRepository.findByRoleName("SYSTEM_ADMIN").orElseThrow();
        admin.getRoles().add(adminRole);

        List<Organization> allOrgs = organizationRepository.findAll();
        admin.getOrganizations().addAll(allOrgs);

        userRepository.save(admin);

        // Organization Admin User
        User orgAdmin = User.builder()
                .username("org.admin")
                .email("org.admin@oasishomehealth.com")
                .password(passwordEncoder.encode("OrgAdmin@123"))
                .firstName("Organization")
                .lastName("Administrator")
                .phoneNumber("555-0150")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        orgAdmin.setIsActive(true);
        orgAdmin.setIsDeleted(false);

        Role orgAdminRole = roleRepository.findByRoleName("ORG_ADMIN").orElseThrow();
        orgAdmin.getRoles().add(orgAdminRole);
        orgAdmin.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(orgAdmin);

        // Intake Coordinator
        User intake = User.builder()
                .username("intake.coordinator")
                .email("intake@oasishomehealth.com")
                .password(passwordEncoder.encode("Intake@123"))
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("555-0201")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        intake.setIsActive(true);
        intake.setIsDeleted(false);

        Role intakeRole = roleRepository.findByRoleName("INTAKE_COORDINATOR").orElseThrow();
        intake.getRoles().add(intakeRole);
        intake.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(intake);

        // RN User
        User rn = User.builder()
                .username("rn.johnson")
                .email("rn.johnson@oasishomehealth.com")
                .password(passwordEncoder.encode("RN@123"))
                .firstName("Mary")
                .lastName("Johnson")
                .phoneNumber("555-0301")
                .licenseNumber("RN123456")
                .licenseState("NY")
                .licenseExpiry(LocalDate.now().plusYears(2))
                .npiNumber("9876543210")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        rn.setIsActive(true);
        rn.setIsDeleted(false);

        Role rnRole = roleRepository.findByRoleName("RN").orElseThrow();
        rn.getRoles().add(rnRole);
        rn.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(rn);

        // QA Nurse
        User qaNurse = User.builder()
                .username("qa.nurse")
                .email("qa.nurse@oasishomehealth.com")
                .password(passwordEncoder.encode("QANurse@123"))
                .firstName("Sarah")
                .lastName("Williams")
                .phoneNumber("555-0401")
                .licenseNumber("RN789012")
                .licenseState("NY")
                .licenseExpiry(LocalDate.now().plusYears(2))
                .npiNumber("9876543211")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        qaNurse.setIsActive(true);
        qaNurse.setIsDeleted(false);

        Role qaNurseRole = roleRepository.findByRoleName("QA_NURSE").orElseThrow();
        qaNurse.getRoles().add(qaNurseRole);
        qaNurse.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(qaNurse);

        // Clinical Manager
        User clinicalManager = User.builder()
                .username("clinical.manager")
                .email("clinical.manager@oasishomehealth.com")
                .password(passwordEncoder.encode("Clinical@123"))
                .firstName("Robert")
                .lastName("Brown")
                .phoneNumber("555-0501")
                .licenseNumber("RN345678")
                .licenseState("NY")
                .licenseExpiry(LocalDate.now().plusYears(2))
                .npiNumber("9876543212")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        clinicalManager.setIsActive(true);
        clinicalManager.setIsDeleted(false);

        Role clinicalManagerRole = roleRepository.findByRoleName("CLINICAL_MANAGER").orElseThrow();
        clinicalManager.getRoles().add(clinicalManagerRole);
        clinicalManager.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(clinicalManager);

        // PT (Physical Therapist)
        User pt = User.builder()
                .username("pt.therapist")
                .email("pt.therapist@oasishomehealth.com")
                .password(passwordEncoder.encode("PT@123"))
                .firstName("David")
                .lastName("Miller")
                .phoneNumber("555-0601")
                .licenseNumber("PT123456")
                .licenseState("NY")
                .licenseExpiry(LocalDate.now().plusYears(2))
                .npiNumber("9876543213")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        pt.setIsActive(true);
        pt.setIsDeleted(false);

        Role ptRole = roleRepository.findByRoleName("PT").orElseThrow();
        pt.getRoles().add(ptRole);
        pt.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(pt);

        // Scheduler
        User scheduler = User.builder()
                .username("scheduler")
                .email("scheduler@oasishomehealth.com")
                .password(passwordEncoder.encode("Scheduler@123"))
                .firstName("Lisa")
                .lastName("Anderson")
                .phoneNumber("555-0701")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        scheduler.setIsActive(true);
        scheduler.setIsDeleted(false);

        Role schedulerRole = roleRepository.findByRoleName("SCHEDULER").orElseThrow();
        scheduler.getRoles().add(schedulerRole);
        scheduler.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(scheduler);

        // Billing Specialist
        User billing = User.builder()
                .username("billing")
                .email("billing@oasishomehealth.com")
                .password(passwordEncoder.encode("Billing@123"))
                .firstName("Michael")
                .lastName("Davis")
                .phoneNumber("555-0801")
                .isEmailVerified(true)
                .isLocked(false)
                .roles(new HashSet<>())
                .organizations(new HashSet<>())
                .build();
        billing.setIsActive(true);
        billing.setIsDeleted(false);

        Role billingRole = roleRepository.findByRoleName("BILLING_SPECIALIST").orElseThrow();
        billing.getRoles().add(billingRole);
        billing.getOrganizations().add(organizationRepository.findByOrganizationCode("OASIS-NYC-01").orElseThrow());

        userRepository.save(billing);

        logger.info("Initialized 9 users");
        logger.info("Login credentials:");
        logger.info("  System Admin: admin / Admin@123");
        logger.info("  Organization Admin: org.admin / OrgAdmin@123");
        logger.info("  Intake Coordinator: intake.coordinator / Intake@123");
        logger.info("  RN: rn.johnson / RN@123");
        logger.info("  QA Nurse: qa.nurse / QANurse@123");
        logger.info("  Clinical Manager: clinical.manager / Clinical@123");
        logger.info("  PT: pt.therapist / PT@123");
        logger.info("  Scheduler: scheduler / Scheduler@123");
        logger.info("  Billing Specialist: billing / Billing@123");
    }
}

