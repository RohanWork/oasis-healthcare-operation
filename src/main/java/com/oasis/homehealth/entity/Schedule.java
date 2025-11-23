package com.oasis.homehealth.entity;

import com.oasis.homehealth.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules", indexes = {
    @Index(name = "idx_schedule_user", columnList = "user_id"),
    @Index(name = "idx_schedule_date", columnList = "schedule_date"),
    @Index(name = "idx_schedule_org", columnList = "organization_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "availability", nullable = false, length = 20)
    private String availability; 
    // AVAILABLE, BUSY, OFF, PTO, SICK, HOLIDAY, TRAINING

    @Column(name = "max_visits")
    private Integer maxVisits; // Maximum visits clinician can handle this day

    @Column(name = "scheduled_visits")
    private Integer scheduledVisits; // Current count

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "is_recurring")
    private Boolean isRecurring; // For regular schedule patterns

    @Column(name = "day_of_week")
    private Integer dayOfWeek; // 1=Monday, 7=Sunday (for recurring)

    // Helper Methods
    public Boolean isAvailable() {
        return "AVAILABLE".equals(availability);
    }

    public Boolean hasCapacity() {
        if (maxVisits == null || scheduledVisits == null) {
            return true;
        }
        return scheduledVisits < maxVisits;
    }

    public Integer getRemainingCapacity() {
        if (maxVisits == null || scheduledVisits == null) {
            return null;
        }
        return maxVisits - scheduledVisits;
    }
}

