package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patient_mrn", columnList = "medical_record_number"),
    @Index(name = "idx_patient_ssn", columnList = "ssn"),
    @Index(name = "idx_patient_org", columnList = "organization_id"),
    @Index(name = "idx_patient_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    // Organization (Multi-tenant)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // Medical Record Number
    @Column(name = "medical_record_number", unique = true, nullable = false, length = 20)
    private String medicalRecordNumber;

    // Personal Information
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender; // MALE, FEMALE, OTHER

    @Column(name = "ssn", length = 11)
    private String ssn;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus; // SINGLE, MARRIED, DIVORCED, WIDOWED

    @Column(name = "race", length = 50)
    private String race;

    @Column(name = "ethnicity", length = 50)
    private String ethnicity;

    @Column(name = "language", length = 50)
    private String language;

    // Contact Information
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "email", length = 100)
    private String email;

    // Address
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

    @Column(name = "county", length = 100)
    private String county;

    // Emergency Contact
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    // Physician Information
    @Column(name = "primary_physician_name", length = 100)
    private String primaryPhysicianName;

    @Column(name = "primary_physician_phone", length = 20)
    private String primaryPhysicianPhone;

    @Column(name = "primary_physician_npi", length = 10)
    private String primaryPhysicianNpi;

    // Referral Information
    @Column(name = "referral_source", length = 100)
    private String referralSource;

    @Column(name = "referral_date")
    private LocalDate referralDate;

    @Column(name = "referral_diagnosis", length = 500)
    private String referralDiagnosis;

    // Admission Information
    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "discharge_date")
    private LocalDate dischargeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admitting_clinician_id")
    private User admittingClinician;

    // Status
    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, ACTIVE, DISCHARGED, DECEASED, TRANSFERRED

    @Column(name = "status_reason", length = 500)
    private String statusReason;

    // Clinical Information
    @Column(name = "allergies", length = 1000)
    private String allergies;

    @Column(name = "medications", length = 2000)
    private String medications;

    @Column(name = "medical_history", length = 2000)
    private String medicalHistory;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    // Insurance (One-to-Many relationship)
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Insurance> insurances = new HashSet<>();

    // Episodes (One-to-Many relationship)
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Episode> episodes = new HashSet<>();

    // Helper methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }

    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null) address.append(addressLine1);
        if (addressLine2 != null) address.append(", ").append(addressLine2);
        if (city != null) address.append(", ").append(city);
        if (state != null) address.append(", ").append(state);
        if (zipCode != null) address.append(" ").append(zipCode);
        return address.toString();
    }

    public Integer getAge() {
        if (dateOfBirth == null) return null;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public void addInsurance(Insurance insurance) {
        insurances.add(insurance);
        insurance.setPatient(this);
    }

    public void removeInsurance(Insurance insurance) {
        insurances.remove(insurance);
        insurance.setPatient(null);
    }
}

