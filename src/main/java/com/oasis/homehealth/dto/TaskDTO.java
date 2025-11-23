package com.oasis.homehealth.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String taskNumber;
    
    // Related entities
    private Long patientId;
    private String patientName;
    private Long episodeId;
    private String episodeNumber;
    private Long planOfCareId;
    private String pocNumber;
    private Long organizationId;
    private Long assignedToId;
    private String assignedToName;
    
    // Task info
    private String taskType;
    private String title;
    private String description;
    
    // Scheduling
    private LocalDate scheduledDate;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private Integer estimatedDurationMinutes;
    
    // Actual time
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Integer actualDurationMinutes;
    
    // Status
    private String status;
    private String statusReason;
    private String priority;
    private Boolean isUrgent;
    
    // Completion
    private Long completedById;
    private String completedByName;
    private LocalDateTime completedAt;
    private String completionNotes;
    
    // QA
    private Long qaReviewedById;
    private String qaReviewedByName;
    private LocalDateTime qaReviewedAt;
    private String qaComments;
    private String qaStatus;
    
    // Cancellation
    private Long cancelledById;
    private String cancelledByName;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private LocalDate originalScheduledDate;
    private Integer rescheduleCount;
    
    // Visit details
    private String visitLocation;
    private Integer travelTimeMinutes;
    private Double mileage;
    
    // Recurrence
    private Boolean isRecurring;
    private String recurrencePattern;
    private Long parentTaskId;
    
    // Reminders
    private Boolean reminderSent;
    private LocalDateTime reminderSentAt;
    private Boolean confirmationRequired;
    private Boolean confirmationReceived;
    
    // Additional
    private String specialInstructions;
    private String patientAvailability;
    private String notes;
    
    // Billable
    private Boolean isBillable;
    private String billingCode;
    private Integer billingUnits;
    private Boolean billed;
    
    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // Calculated
    private Boolean isOverdue;
    private Boolean isDueToday;
    private Boolean isDueTomorrow;
    private Integer daysUntilDue;
    private String disciplineCode;
    private Boolean canBeEdited;
    private Boolean canBeCancelled;
    private Boolean needsQAReview;
}
