package com.oasis.homehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingClaimDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long episodeId;
    private String episodeNumber;
    private Long organizationId;
    private String organizationName;
    private Long visitNoteId;
    private Long taskId;

    private String claimNumber;
    private String claimType;
    private LocalDate billingDate;
    private LocalDate serviceDate;
    private LocalDate serviceEndDate;

    private String serviceCode;
    private String serviceDescription;
    private Integer units;
    private BigDecimal unitRate;
    private BigDecimal totalCharge;

    private Long insuranceId;
    private String insuranceName;
    private String insuranceClaimNumber;

    private String status;
    private LocalDate submittedDate;
    private LocalDate paidDate;
    private BigDecimal paidAmount;
    private LocalDate deniedDate;
    private String denialReason;

    private BigDecimal patientResponsibility;
    private BigDecimal insurancePayment;
    private BigDecimal adjustmentAmount;
    private String adjustmentReason;

    private String notes;
    private Long billedByUserId;
    private String billedByName;
    private LocalDateTime billedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    // Calculated
    private BigDecimal outstandingBalance;
    private Boolean isPaid;
    private Boolean isDenied;
    private Boolean canBeEdited;
}

