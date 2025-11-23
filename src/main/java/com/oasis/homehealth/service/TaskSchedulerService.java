package com.oasis.homehealth.service;

import com.oasis.homehealth.dto.*;
import com.oasis.homehealth.entity.*;
import com.oasis.homehealth.repository.*;
import com.oasis.homehealth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

    private final TaskRepository taskRepository;
    private final VisitNoteRepository visitNoteRepository;
    private final PatientRepository patientRepository;
    private final EpisodeRepository episodeRepository;
    private final PlanOfCareRepository pocRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    
    /**
     * AUTO-GENERATE TASKS FROM PLAN OF CARE ⭐⭐⭐
     * This is the killer feature!
     * 
     * Example: POC has "RN: 3W8" (3 visits/week for 8 weeks)
     * → This method creates 24 RN visit tasks spread across 8 weeks
     */
    @Transactional
    public List<TaskDTO> generateTasksFromPOC(TaskGenerationRequest request) {
        log.info("Generating tasks from Plan of Care: {}", request.getPlanOfCareId());
        
        PlanOfCare poc = pocRepository.findById(request.getPlanOfCareId())
            .orElseThrow(() -> new RuntimeException("Plan of Care not found"));
            
        // Allow task generation for DRAFT, APPROVED, or ACTIVE POCs
        // DRAFT is allowed because tasks are auto-generated when POC is created
        if (!"DRAFT".equals(poc.getStatus()) && !"APPROVED".equals(poc.getStatus()) && !"ACTIVE".equals(poc.getStatus())) {
            throw new RuntimeException("Plan of Care must be in DRAFT, APPROVED, or ACTIVE status before generating tasks");
        }
        
        // Check if tasks already generated
        List<Task> existing = taskRepository.findByPlanOfCareId(poc.getId());
        if (!existing.isEmpty()) {
            log.warn("Tasks already exist for POC: {}. Found {} tasks.", poc.getPocNumber(), existing.size());
            // Return existing tasks instead of error (idempotent)
            return existing.stream().map(this::convertToDTO).collect(Collectors.toList());
        }
        
        List<Task> generatedTasks = new ArrayList<>();
        
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : poc.getStartDate();
        User assignedClinician = null;
        
        if (request.getAssignToClinicianId() != null) {
            assignedClinician = userRepository.findById(request.getAssignToClinicianId()).orElse(null);
        } else if (poc.getPatient().getAdmittingClinician() != null) {
            assignedClinician = poc.getPatient().getAdmittingClinician();
        }
        
        // Get current task count to use as base for unique task numbers
        long baseTaskCount = taskRepository.count();
        int taskSequence = 0; // Counter for tasks in this batch
        
        // Generate tasks for each frequency in the POC
        for (PlanOfCareFrequency frequency : poc.getFrequencies()) {
            if (Boolean.FALSE.equals(frequency.getIsActive())) {
                continue;
            }
            
            // Check if we should generate this discipline
            if (request.getDisciplinesToGenerate() != null && 
                !request.getDisciplinesToGenerate().isEmpty() &&
                !request.getDisciplinesToGenerate().contains(frequency.getDisciplineType())) {
                continue;
            }
            
            List<Task> disciplineTasks = generateTasksForFrequency(
                poc, frequency, startDate, assignedClinician, baseTaskCount, taskSequence);
            generatedTasks.addAll(disciplineTasks);
            taskSequence += disciplineTasks.size(); // Increment sequence counter
        }
        
        // Generate OASIS recertification task (60 days from POC start)
        if (poc.getEndDate() != null) {
            Task recertTask = generateOASISRecertTask(poc, assignedClinician, baseTaskCount + taskSequence);
            generatedTasks.add(recertTask);
            taskSequence++; // Increment for recert task
        }
        
        // Save all tasks
        generatedTasks = taskRepository.saveAll(generatedTasks);
        
        log.info("Successfully generated {} tasks from POC: {}", generatedTasks.size(), poc.getPocNumber());
        
        return generatedTasks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Generate tasks for a specific frequency (e.g., RN: 3W8)
     */
    private List<Task> generateTasksForFrequency(PlanOfCare poc, PlanOfCareFrequency frequency, 
                                                   LocalDate startDate, User assignedClinician,
                                                   long baseTaskCount, int startSequence) {
        List<Task> tasks = new ArrayList<>();
        
        Integer visitsPerWeek = frequency.getVisitsPerWeek();
        Integer numberOfWeeks = frequency.getNumberOfWeeks();
        
        if (visitsPerWeek == null || numberOfWeeks == null) {
            log.warn("Frequency {} has null visits/week or weeks. Skipping.", frequency.getDisciplineType());
            return tasks;
        }
        
        // Calculate total visits
        int totalVisits = visitsPerWeek * numberOfWeeks;
        
        // Distribute visits across weeks
        LocalDate currentDate = startDate;
        int visitsCreated = 0;
        int sequenceCounter = startSequence;
        
        for (int week = 0; week < numberOfWeeks && visitsCreated < totalVisits; week++) {
            // Distribute visits within the week
            List<LocalDate> weekDates = getVisitDatesForWeek(currentDate, visitsPerWeek);
            
            for (LocalDate visitDate : weekDates) {
                if (visitsCreated >= totalVisits) break;
                
                // Use disciplineDescription if available, otherwise use disciplineType
                String disciplineName = (frequency.getDisciplineDescription() != null && 
                                        !frequency.getDisciplineDescription().trim().isEmpty())
                    ? frequency.getDisciplineDescription()
                    : frequency.getDisciplineType();
                
                Task task = Task.builder()
                    .patient(poc.getPatient())
                    .episode(poc.getEpisode())
                    .planOfCare(poc)
                    .organization(poc.getOrganization())
                    .taskNumber(generateTaskNumber(poc.getOrganization(), baseTaskCount + sequenceCounter))
                    .taskType(frequency.getDisciplineType() + "_VISIT")
                    .title(disciplineName + " Visit")
                    .description(String.format("%s visit for %s", 
                        disciplineName, 
                        poc.getPatient().getFullName()))
                    .scheduledDate(visitDate)
                    .estimatedDurationMinutes(frequency.getEstimatedMinutesPerVisit())
                    .status("SCHEDULED")
                    .priority("NORMAL")
                    .isUrgent(false)
                    .assignedTo(assignedClinician)
                    .visitLocation(poc.getPatient().getFullAddress())
                    .isBillable(true)
                    .billed(false)
                    .rescheduleCount(0)
                    .build();
                
                tasks.add(task);
                visitsCreated++;
                sequenceCounter++; // Increment for next task
            }
            
            // Move to next week
            currentDate = currentDate.plusWeeks(1);
        }
        
        log.info("Generated {} {} tasks for frequency: {}", 
            tasks.size(), frequency.getDisciplineType(), frequency.getFrequencyCode());
        
        return tasks;
    }
    
    /**
     * Distribute visits across a week (avoiding weekends by default)
     */
    private List<LocalDate> getVisitDatesForWeek(LocalDate weekStart, int visitsPerWeek) {
        List<LocalDate> dates = new ArrayList<>();
        
        // Start from the given week start date
        LocalDate current = weekStart;
        
        // Move to Monday if not already
        while (current.getDayOfWeek() == DayOfWeek.SATURDAY || current.getDayOfWeek() == DayOfWeek.SUNDAY) {
            current = current.plusDays(1);
        }
        
        // Distribute visits (e.g., 3 visits/week → Mon, Wed, Fri)
        int[] dayGaps = calculateDayGaps(visitsPerWeek);
        
        for (int i = 0; i < visitsPerWeek; i++) {
            // Skip weekends
            while (current.getDayOfWeek() == DayOfWeek.SATURDAY || current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                current = current.plusDays(1);
            }
            
            dates.add(current);
            
            if (i < dayGaps.length) {
                current = current.plusDays(dayGaps[i]);
            }
        }
        
        return dates;
    }
    
    /**
     * Calculate optimal day gaps between visits
     * Example: 3 visits/week → [2, 2] days (Mon, Wed, Fri)
     */
    private int[] calculateDayGaps(int visitsPerWeek) {
        switch (visitsPerWeek) {
            case 1: return new int[]{0}; // Once per week
            case 2: return new int[]{3}; // Mon, Thu
            case 3: return new int[]{2, 2}; // Mon, Wed, Fri
            case 4: return new int[]{2, 1, 2}; // Mon, Wed, Thu, Mon (next week)
            case 5: return new int[]{1, 1, 1, 1}; // Mon-Fri
            default: return new int[]{1}; // Daily
        }
    }
    
    /**
     * Generate OASIS recertification task (60 days from POC start)
     */
    private Task generateOASISRecertTask(PlanOfCare poc, User assignedClinician, long sequenceNumber) {
        // Recert should be completed ~5 days before POC ends
        LocalDate recertDate = poc.getEndDate().minusDays(5);
        
        return Task.builder()
            .patient(poc.getPatient())
            .episode(poc.getEpisode())
            .planOfCare(poc)
            .organization(poc.getOrganization())
            .taskNumber(generateTaskNumber(poc.getOrganization(), sequenceNumber))
            .taskType("OASIS_RECERT")
            .title("OASIS Recertification Assessment")
            .description("Complete OASIS recertification assessment for " + poc.getPatient().getFullName())
            .scheduledDate(recertDate)
            .estimatedDurationMinutes(90)
            .status("SCHEDULED")
            .priority("HIGH")
            .isUrgent(false)
            .assignedTo(assignedClinician)
            .isBillable(false)
            .billed(false)
            .rescheduleCount(0)
            .build();
    }
    
    /**
     * Create task manually
     */
    @Transactional
    public TaskDTO createTask(TaskRequest request, Long organizationId) {
        log.info("Creating task for patient: {}", request.getPatientId());
        
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));
            
        Episode episode = episodeRepository.findById(request.getEpisodeId())
            .orElseThrow(() -> new RuntimeException("Episode not found"));
            
        Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId()).orElse(null);
        }
        
        PlanOfCare poc = null;
        if (request.getPlanOfCareId() != null) {
            poc = pocRepository.findById(request.getPlanOfCareId()).orElse(null);
        }
        
        Task task = Task.builder()
            .patient(patient)
            .episode(episode)
            .planOfCare(poc)
            .organization(organization)
            .taskNumber(generateTaskNumber(organization))
            .taskType(request.getTaskType())
            .title(request.getTitle())
            .description(request.getDescription())
            .scheduledDate(request.getScheduledDate())
            .scheduledStartTime(request.getScheduledStartTime())
            .scheduledEndTime(request.getScheduledEndTime())
            .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
            .status(request.getStatus() != null ? request.getStatus() : "SCHEDULED")
            .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
            .isUrgent(request.getIsUrgent() != null ? request.getIsUrgent() : false)
            .assignedTo(assignedTo)
            .visitLocation(request.getVisitLocation())
            .specialInstructions(request.getSpecialInstructions())
            .patientAvailability(request.getPatientAvailability())
            .notes(request.getNotes())
            .isBillable(request.getIsBillable())
            .billingCode(request.getBillingCode())
            .billed(false)
            .rescheduleCount(0)
            .build();
        
        task = taskRepository.save(task);
        
        log.info("Successfully created task: {}", task.getTaskNumber());
        return convertToDTO(task);
    }
    
    /**
     * Update task
     */
    @Transactional
    public TaskDTO updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        if (!task.canBeEdited()) {
            throw new RuntimeException("Task cannot be edited in current status: " + task.getStatus());
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setScheduledDate(request.getScheduledDate());
        task.setScheduledStartTime(request.getScheduledStartTime());
        task.setScheduledEndTime(request.getScheduledEndTime());
        task.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        task.setPriority(request.getPriority());
        task.setIsUrgent(request.getIsUrgent());
        task.setVisitLocation(request.getVisitLocation());
        task.setSpecialInstructions(request.getSpecialInstructions());
        task.setPatientAvailability(request.getPatientAvailability());
        task.setNotes(request.getNotes());
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId()).orElse(null);
            task.setAssignedTo(assignedTo);
        }
        
        task = taskRepository.save(task);
        return convertToDTO(task);
    }
    
    /**
     * Reschedule task
     */
    @Transactional
    public TaskDTO rescheduleTask(Long id, LocalDate newDate, String reason) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        if (!task.canBeEdited()) {
            throw new RuntimeException("Task cannot be rescheduled in current status: " + task.getStatus());
        }
        
        if (task.getOriginalScheduledDate() == null) {
            task.setOriginalScheduledDate(task.getScheduledDate());
        }
        
        task.setScheduledDate(newDate);
        task.setStatus("RESCHEDULED");
        task.setStatusReason(reason);
        task.setRescheduleCount((task.getRescheduleCount() != null ? task.getRescheduleCount() : 0) + 1);
        
        task = taskRepository.save(task);
        
        log.info("Task {} rescheduled to {}", task.getTaskNumber(), newDate);
        return convertToDTO(task);
    }
    
    /**
     * Assign task to clinician
     */
    @Transactional
    public TaskDTO assignTask(Long id, Long clinicianId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        User clinician = userRepository.findById(clinicianId)
            .orElseThrow(() -> new RuntimeException("Clinician not found"));
            
        task.setAssignedTo(clinician);
        task = taskRepository.save(task);
        
        log.info("Task {} assigned to {}", task.getTaskNumber(), clinician.getFullName());
        return convertToDTO(task);
    }
    
    /**
     * Cancel task
     */
    @Transactional
    public TaskDTO cancelTask(Long id, String reason) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        if (!task.canBeCancelled()) {
            throw new RuntimeException("Task cannot be cancelled in current status: " + task.getStatus());
        }
        
        UserPrincipal currentUser = getCurrentUser();
        User cancelledBy = userRepository.findById(currentUser.getId()).orElse(null);
        
        task.setStatus("CANCELLED");
        task.setCancelledBy(cancelledBy);
        task.setCancelledAt(LocalDateTime.now());
        task.setCancellationReason(reason);
        
        task = taskRepository.save(task);
        
        log.info("Task {} cancelled", task.getTaskNumber());
        return convertToDTO(task);
    }
    
    /**
     * Start a task (mark as IN_PROGRESS)
     */
    @Transactional
    public TaskDTO startTask(Long id, Long organizationId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Validate organization access
        if (organizationId != null && !task.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Task does not belong to your organization");
        }
        
        if (!"SCHEDULED".equals(task.getStatus()) && !"RESCHEDULED".equals(task.getStatus()) && !"PENDING".equals(task.getStatus())) {
            throw new RuntimeException("Task cannot be started. Current status: " + task.getStatus() + ". Task must be in SCHEDULED, RESCHEDULED, or PENDING status.");
        }
        
        task.setStatus("IN_PROGRESS");
        task.setActualStartTime(LocalDateTime.now());
        task = taskRepository.save(task);
        
        // Update patient status from PENDING to ACTIVE when first task is started
        updatePatientStatusIfPending(task.getPatient());
        
        return convertToDTO(task);
    }

    /**
     * Complete task
     */
    @Transactional
    public TaskDTO completeTask(Long id, String completionNotes, Long organizationId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Validate organization access
        if (organizationId != null && !task.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Task does not belong to your organization");
        }
            
        UserPrincipal currentUser = getCurrentUser();
        User completedBy = userRepository.findById(currentUser.getId()).orElse(null);
        
        task.setStatus("COMPLETED_PENDING_QA");
        task.setCompletedBy(completedBy);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletionNotes(completionNotes);
        
        if (task.getActualStartTime() == null) {
            task.setActualStartTime(LocalDateTime.now().minusHours(1)); // Assume 1 hour visit
        }
        if (task.getActualEndTime() == null) {
            task.setActualEndTime(LocalDateTime.now());
        }
        
        task.calculateActualDuration();
        task.calculateBillingUnits();
        
        task = taskRepository.save(task);
        
        log.info("Task {} completed", task.getTaskNumber());
        return convertToDTO(task);
    }
    
    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id, Long organizationId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Validate organization access (if organizationId is provided)
        // SYSTEM_ADMIN can access tasks from any organization (organizationId will be null)
        if (organizationId != null && !task.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Task does not belong to your organization");
        }
        
        return convertToDTO(task);
    }
    
    /**
     * Get all tasks for organization
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks(Long organizationId) {
        // If organizationId is null, get all tasks (for SYSTEM_ADMIN)
        if (organizationId == null) {
            return taskRepository.findAll().stream()
                .filter(task -> !Boolean.TRUE.equals(task.getIsDeleted()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }
        
        return taskRepository.findByOrganizationId(organizationId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get tasks for clinician (their assigned tasks)
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForClinician(Long clinicianId) {
        return taskRepository.findByAssignedToId(clinicianId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get tasks by patient
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByPatient(Long patientId) {
        return taskRepository.findByPatientId(patientId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get tasks by date range
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByDateRange(Long organizationId, LocalDate startDate, LocalDate endDate) {
        // If organizationId is null, get all tasks in date range (for SYSTEM_ADMIN)
        if (organizationId == null) {
            return taskRepository.findAll().stream()
                .filter(task -> !Boolean.TRUE.equals(task.getIsDeleted()) &&
                               task.getScheduledDate() != null &&
                               !task.getScheduledDate().isBefore(startDate) &&
                               !task.getScheduledDate().isAfter(endDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }
        
        return taskRepository.findByOrganizationIdAndDateRange(organizationId, startDate, endDate).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get clinician tasks by date range (for calendar view)
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getClinicianTasksByDateRange(Long clinicianId, LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByAssignedToIdAndDateRange(clinicianId, startDate, endDate).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get tasks due today
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksDueToday(Long organizationId) {
        // If organizationId is null, get all tasks due today (for SYSTEM_ADMIN)
        if (organizationId == null) {
            LocalDate today = LocalDate.now();
            return taskRepository.findAll().stream()
                .filter(task -> !Boolean.TRUE.equals(task.getIsDeleted()) &&
                               task.getScheduledDate() != null &&
                               task.getScheduledDate().equals(today) &&
                               (task.getStatus() == null || 
                                "SCHEDULED".equals(task.getStatus()) || 
                                "RESCHEDULED".equals(task.getStatus())))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }
        
        return taskRepository.findDueToday(organizationId, LocalDate.now()).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get overdue tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getOverdueTasks(Long organizationId) {
        // If organizationId is null, get all overdue tasks (for SYSTEM_ADMIN)
        if (organizationId == null) {
            LocalDate today = LocalDate.now();
            return taskRepository.findAll().stream()
                .filter(task -> !Boolean.TRUE.equals(task.getIsDeleted()) &&
                               task.getScheduledDate() != null &&
                               task.getScheduledDate().isBefore(today) &&
                               (task.getStatus() == null || 
                                "SCHEDULED".equals(task.getStatus()) || 
                                "RESCHEDULED".equals(task.getStatus())))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }
        
        return taskRepository.findOverdue(organizationId, LocalDate.now()).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Delete task (soft delete)
     */
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
            
        if (!task.canBeCancelled()) {
            throw new RuntimeException("Task cannot be deleted in current status: " + task.getStatus());
        }
        
        task.setIsDeleted(true);
        task.setIsActive(false);
        taskRepository.save(task);
    }
    
    // ==================== HELPER METHODS ====================
    
    private String generateTaskNumber(Organization organization) {
        // Use count-based approach for single task generation
        long count = taskRepository.count() + 1;
        return generateTaskNumber(organization, count);
    }
    
    private String generateTaskNumber(Organization organization, long sequenceNumber) {
        String orgCode = organization.getOrganizationCode() != null ? 
            organization.getOrganizationCode() : 
            (organization.getOrganizationName() != null ? 
                organization.getOrganizationName().substring(0, Math.min(3, organization.getOrganizationName().length())).toUpperCase() : 
                "ORG");
        return String.format("TSK-%s-%08d", orgCode, sequenceNumber);
    }
    
    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
            .id(task.getId())
            .taskNumber(task.getTaskNumber())
            .patientId(task.getPatient().getId())
            .patientName(task.getPatient().getFullName())
            .episodeId(task.getEpisode().getId())
            .episodeNumber(task.getEpisode().getEpisodeNumber())
            .planOfCareId(task.getPlanOfCare() != null ? task.getPlanOfCare().getId() : null)
            .pocNumber(task.getPlanOfCare() != null ? task.getPlanOfCare().getPocNumber() : null)
            .organizationId(task.getOrganization().getId())
            .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
            .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getFullName() : null)
            .taskType(task.getTaskType())
            .title(task.getTitle())
            .description(task.getDescription())
            .scheduledDate(task.getScheduledDate())
            .scheduledStartTime(task.getScheduledStartTime())
            .scheduledEndTime(task.getScheduledEndTime())
            .estimatedDurationMinutes(task.getEstimatedDurationMinutes())
            .actualStartTime(task.getActualStartTime())
            .actualEndTime(task.getActualEndTime())
            .actualDurationMinutes(task.getActualDurationMinutes())
            .status(task.getStatus())
            .statusReason(task.getStatusReason())
            .priority(task.getPriority())
            .isUrgent(task.getIsUrgent())
            .completedById(task.getCompletedBy() != null ? task.getCompletedBy().getId() : null)
            .completedByName(task.getCompletedBy() != null ? task.getCompletedBy().getFullName() : null)
            .completedAt(task.getCompletedAt())
            .completionNotes(task.getCompletionNotes())
            .qaReviewedById(task.getQaReviewedBy() != null ? task.getQaReviewedBy().getId() : null)
            .qaReviewedByName(task.getQaReviewedBy() != null ? task.getQaReviewedBy().getFullName() : null)
            .qaReviewedAt(task.getQaReviewedAt())
            .qaComments(task.getQaComments())
            .qaStatus(task.getQaStatus())
            .cancelledById(task.getCancelledBy() != null ? task.getCancelledBy().getId() : null)
            .cancelledByName(task.getCancelledBy() != null ? task.getCancelledBy().getFullName() : null)
            .cancelledAt(task.getCancelledAt())
            .cancellationReason(task.getCancellationReason())
            .originalScheduledDate(task.getOriginalScheduledDate())
            .rescheduleCount(task.getRescheduleCount())
            .visitLocation(task.getVisitLocation())
            .travelTimeMinutes(task.getTravelTimeMinutes())
            .mileage(task.getMileage())
            .isRecurring(task.getIsRecurring())
            .recurrencePattern(task.getRecurrencePattern())
            .parentTaskId(task.getParentTaskId())
            .reminderSent(task.getReminderSent())
            .reminderSentAt(task.getReminderSentAt())
            .confirmationRequired(task.getConfirmationRequired())
            .confirmationReceived(task.getConfirmationReceived())
            .specialInstructions(task.getSpecialInstructions())
            .patientAvailability(task.getPatientAvailability())
            .notes(task.getNotes())
            .isBillable(task.getIsBillable())
            .billingCode(task.getBillingCode())
            .billingUnits(task.getBillingUnits())
            .billed(task.getBilled())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .createdBy(task.getCreatedBy())
            .updatedBy(task.getUpdatedBy())
            .isOverdue(task.isOverdue())
            .isDueToday(task.isDueToday())
            .isDueTomorrow(task.isDueTomorrow())
            .daysUntilDue(task.getDaysUntilDue())
            .disciplineCode(task.getDisciplineCode())
            .canBeEdited(task.canBeEdited())
            .canBeCancelled(task.canBeCancelled())
            .needsQAReview(task.needsQAReview())
            .build();
    }
    
    /**
     * Get tasks pending QA review
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getPendingQAReview(Long organizationId) {
        log.info("Fetching tasks pending QA review for organization: {}", organizationId);
        
        List<Task> tasks;
        if (organizationId == null) {
            // SYSTEM_ADMIN can see all pending tasks
            tasks = taskRepository.findByStatusAndIsDeletedFalse("COMPLETED_PENDING_QA");
        } else {
            tasks = taskRepository.findPendingQAReview(organizationId);
        }
        
        log.info("Found {} tasks pending QA review", tasks.size());
        return tasks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Approve task (QA approval)
     */
    @Transactional
    public TaskDTO approveTask(Long id, Long organizationId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Validate organization access
        if (organizationId != null && !task.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Task does not belong to your organization");
        }
        
        if (!"COMPLETED_PENDING_QA".equals(task.getStatus())) {
            throw new RuntimeException("Only tasks with status COMPLETED_PENDING_QA can be approved");
        }
        
        UserPrincipal currentUser = getCurrentUser();
        User approvedBy = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        task.setStatus("QA_APPROVED");
        task.setQaReviewedBy(approvedBy);
        task.setQaReviewedAt(LocalDateTime.now());
        task.setQaStatus("APPROVED");
        task = taskRepository.save(task);
        
        log.info("Task {} approved by QA", task.getTaskNumber());
        return convertToDTO(task);
    }
    
    /**
     * Reject task (QA rejection - return for correction)
     */
    @Transactional
    public TaskDTO rejectTask(Long id, String rejectionReason, Long organizationId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Validate organization access
        if (organizationId != null && !task.getOrganization().getId().equals(organizationId)) {
            throw new RuntimeException("Task does not belong to your organization");
        }
        
        if (!"COMPLETED_PENDING_QA".equals(task.getStatus())) {
            throw new RuntimeException("Only tasks with status COMPLETED_PENDING_QA can be rejected");
        }
        
        UserPrincipal currentUser = getCurrentUser();
        User rejectedBy = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        task.setStatus("RETURNED_FOR_CORRECTION");
        task.setQaReviewedBy(rejectedBy);
        task.setQaReviewedAt(LocalDateTime.now());
        task.setQaComments(rejectionReason);
        task.setQaStatus("RETURNED");
        task = taskRepository.save(task);
        
        log.info("Task {} rejected by QA: {}", task.getTaskNumber(), rejectionReason);
        return convertToDTO(task);
    }
    
    /**
     * Update patient status from PENDING to ACTIVE when care begins
     */
    private void updatePatientStatusIfPending(Patient patient) {
        if (patient != null && "PENDING".equals(patient.getStatus())) {
            patient.setStatus("ACTIVE");
            patient.setStatusReason("Care started - first task initiated");
            patientRepository.save(patient);
            log.info("Patient {} status updated from PENDING to ACTIVE", patient.getMedicalRecordNumber());
        }
    }
    
    private UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrincipal) authentication.getPrincipal();
    }
}
