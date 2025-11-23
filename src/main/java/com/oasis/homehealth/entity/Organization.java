package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "organizations", indexes = {
    @Index(name = "idx_org_code", columnList = "organization_code"),
    @Index(name = "idx_org_name", columnList = "organization_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Column(name = "organization_code", unique = true, nullable = false, length = 20)
    private String organizationCode;

    @Column(name = "organization_name", nullable = false, length = 200)
    private String organizationName;

    @Column(name = "legal_name", length = 200)
    private String legalName;

    @Column(name = "tax_id", length = 20)
    private String taxId;

    @Column(name = "npi_number", length = 10)
    private String npiNumber;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "fax_number", length = 20)
    private String faxNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 2)
    private String state;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "country", length = 50)
    private String country = "USA";

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "accreditation", length = 100)
    private String accreditation;

    @Column(name = "organization_type", length = 50)
    private String organizationType; // e.g., BRANCH, CORPORATE, REGIONAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_organization_id")
    private Organization parentOrganization;

    @OneToMany(mappedBy = "parentOrganization", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Organization> childOrganizations = new HashSet<>();

    // Many-to-Many relationship with User
    @ManyToMany(mappedBy = "organizations")
    @Builder.Default
    private Set<User> users = new HashSet<>();

    // Subscription/Tenant specific fields
    @Column(name = "subscription_tier", length = 50)
    private String subscriptionTier; // BASIC, PROFESSIONAL, ENTERPRISE

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_patients")
    private Integer maxPatients;

    @Column(name = "is_tenant_active")
    private Boolean isTenantActive = true;

    // Helper method
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null) address.append(addressLine1);
        if (addressLine2 != null) address.append(", ").append(addressLine2);
        if (city != null) address.append(", ").append(city);
        if (state != null) address.append(", ").append(state);
        if (zipCode != null) address.append(" ").append(zipCode);
        return address.toString();
    }
}

