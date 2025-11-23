package com.oasis.homehealth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingClaimRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Episode ID is required")
    private Long episodeId;

    private Long visitNoteId;
    private Long taskId;

    @NotBlank(message = "Claim type is required")
    private String claimType; // RAP, FINAL, ADJUSTMENT

    @NotNull(message = "Billing date is required")
    private LocalDate billingDate;

    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;

    private LocalDate serviceEndDate;

    @NotBlank(message = "Service code is required")
    private String serviceCode;

    private String serviceDescription;

    @NotNull(message = "Units is required")
    @Positive(message = "Units must be positive")
    private Integer units;

    @NotNull(message = "Unit rate is required")
    @Positive(message = "Unit rate must be positive")
    private BigDecimal unitRate;

    private Long insuranceId;
    private String insuranceClaimNumber;

    private String status; // DRAFT, SUBMITTED, PAID, DENIED, ADJUSTED, VOIDED

    private BigDecimal patientResponsibility;
    private BigDecimal adjustmentAmount;
    private String adjustmentReason;

    private String notes;
}

