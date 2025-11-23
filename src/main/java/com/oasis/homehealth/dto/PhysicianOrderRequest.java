package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicianOrderRequest {
    private String orderNumber;
    @NotNull
    private String orderType;
    @NotBlank
    private String orderDescription;
    private String discipline;
    private String frequency;
    private String duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private String orderingPhysicianName;
    private String orderingPhysicianNpi;
    private String orderingPhysicianPhone;
    private LocalDate orderDate;
    private Boolean signatureRequired;
    private Boolean signatureObtained;
    private LocalDate signatureDate;
    private Boolean verbalOrder;
    private String verbalOrderReceivedBy;
    private LocalDate verbalOrderDate;
    private String status;
    private String statusReason;
    private Boolean isActive;
    private String diagnosisCodes;
    private String clinicalJustification;
    private String precautions;
    private String contraindications;
    private Boolean renewable;
    private LocalDate renewalDate;
    private String specialInstructions;
    private String notes;
}

