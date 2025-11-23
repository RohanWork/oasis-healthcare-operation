package com.oasis.homehealth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationRequest {

    @NotBlank(message = "Organization code is required")
    @Size(max = 20, message = "Organization code must not exceed 20 characters")
    private String organizationCode;

    @NotBlank(message = "Organization name is required")
    @Size(max = 200, message = "Organization name must not exceed 200 characters")
    private String organizationName;

    @Size(max = 200, message = "Legal name must not exceed 200 characters")
    private String legalName;

    @Size(max = 20, message = "Tax ID must not exceed 20 characters")
    private String taxId;

    @Size(max = 10, message = "NPI number must not exceed 10 characters")
    private String npiNumber;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 20, message = "Fax number must not exceed 20 characters")
    private String faxNumber;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 200, message = "Website must not exceed 200 characters")
    private String website;

    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 2, message = "State must be 2 characters")
    private String state;

    @Size(max = 10, message = "Zip code must not exceed 10 characters")
    private String zipCode;

    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @Size(max = 100, message = "Accreditation must not exceed 100 characters")
    private String accreditation;

    @Pattern(regexp = "CORPORATE|BRANCH|REGIONAL", message = "Organization type must be CORPORATE, BRANCH, or REGIONAL")
    private String organizationType;

    private Long parentOrganizationId;

    @Pattern(regexp = "BASIC|PROFESSIONAL|ENTERPRISE", message = "Subscription tier must be BASIC, PROFESSIONAL, or ENTERPRISE")
    private String subscriptionTier;

    @Min(value = 1, message = "Max users must be at least 1")
    private Integer maxUsers;

    @Min(value = 1, message = "Max patients must be at least 1")
    private Integer maxPatients;

    private Boolean isTenantActive;
}

