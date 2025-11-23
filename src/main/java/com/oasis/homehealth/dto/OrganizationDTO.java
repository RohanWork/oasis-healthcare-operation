package com.oasis.homehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDTO {

    private Long id;
    private String organizationCode;
    private String organizationName;
    private String legalName;
    private String taxId;
    private String npiNumber;
    private String phoneNumber;
    private String faxNumber;
    private String email;
    private String website;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String organizationType;
    private String subscriptionTier;
    private Boolean isTenantActive;
    private Long parentOrganizationId;
}

