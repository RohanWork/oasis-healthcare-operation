package com.oasis.homehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private String phoneNumber;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private String licenseNumber;
    private String licenseState;
    private LocalDate licenseExpiry;
    private String npiNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private Boolean isEmailVerified;
    private Boolean isLocked;
    private Boolean isActive;
    private LocalDate lastLogin;
    private List<RoleDTO> roles;
    private List<OrganizationDTO> organizations;
}

