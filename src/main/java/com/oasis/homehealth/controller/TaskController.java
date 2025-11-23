package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.*;
import com.oasis.homehealth.security.UserPrincipal;
import com.oasis.homehealth.service.TaskSchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing scheduled visits and tasks")
public class TaskController {

    private final TaskSchedulerService taskSchedulerService;

    @PostMapping("/generate-from-poc")
    @PreAuthorize("hasAuthority('TASK_CREATE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Generate tasks from Plan of Care", 
               description = "Auto-generate visit tasks from approved POC frequencies")
    public ResponseEntity<List<TaskDTO>> generateTasksFromPOC(
            @Valid @RequestBody TaskGenerationRequest request) {
        List<TaskDTO> tasks = taskSchedulerService.generateTasksFromPOC(request);
        return new ResponseEntity<>(tasks, HttpStatus.CREATED);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TASK_CREATE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Create new task", description = "Create a new task manually")
    public ResponseEntity<TaskDTO> createTask(
            @Valid @RequestBody TaskRequest request,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        TaskDTO task = taskSchedulerService.createTask(request, organizationId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TASK_UPDATE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Update task", description = "Update an existing task")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        TaskDTO task = taskSchedulerService.updateTask(id, request);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAuthority('TASK_RESCHEDULE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Reschedule task", description = "Change the scheduled date of a task")
    public ResponseEntity<TaskDTO> rescheduleTask(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
            @RequestParam(required = false) String reason) {
        TaskDTO task = taskSchedulerService.rescheduleTask(id, newDate, reason);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('TASK_ASSIGN') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Assign task to clinician", description = "Assign or reassign a task to a clinician")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long id,
            @RequestParam Long clinicianId) {
        TaskDTO task = taskSchedulerService.assignTask(id, clinicianId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasAuthority('TASK_UPDATE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Start task", description = "Mark a task as in progress")
    public ResponseEntity<TaskDTO> startTask(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        TaskDTO task = taskSchedulerService.startTask(id, organizationId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('TASK_COMPLETE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Complete task", description = "Mark a task as completed")
    public ResponseEntity<TaskDTO> completeTask(
            @PathVariable Long id,
            @RequestParam(required = false) String completionNotes,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        TaskDTO task = taskSchedulerService.completeTask(id, completionNotes, organizationId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('TASK_CANCEL') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Cancel task", description = "Cancel a scheduled task")
    public ResponseEntity<TaskDTO> cancelTask(
            @PathVariable Long id,
            @RequestParam String reason) {
        TaskDTO task = taskSchedulerService.cancelTask(id, reason);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/qa/pending")
    @PreAuthorize("hasAuthority('TASK_APPROVE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_QA_NURSE') or hasRole('ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Get tasks pending QA review", description = "Retrieve tasks that are completed and pending QA review")
    public ResponseEntity<List<TaskDTO>> getPendingQAReview(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // SYSTEM_ADMIN can access all tasks without organization context
        boolean isSystemAdmin = false;
        if (currentUser != null) {
            isSystemAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
        }
        
        if (organizationId == null && !isSystemAdmin) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        
        List<TaskDTO> tasks = taskSchedulerService.getPendingQAReview(organizationId);
        return ResponseEntity.ok(tasks);
    }
    
    @PutMapping("/{id}/qa/approve")
    @PreAuthorize("hasAuthority('TASK_APPROVE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_QA_NURSE') or hasRole('ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Approve task (QA)", description = "Approve a completed task after QA review")
    public ResponseEntity<TaskDTO> approveTask(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        TaskDTO task = taskSchedulerService.approveTask(id, organizationId);
        return ResponseEntity.ok(task);
    }
    
    @PutMapping("/{id}/qa/reject")
    @PreAuthorize("hasAuthority('TASK_APPROVE') or hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_QA_NURSE') or hasRole('ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Reject task (QA)", description = "Reject a completed task and return for correction")
    public ResponseEntity<TaskDTO> rejectTask(
            @PathVariable Long id,
            @RequestParam String reason,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        TaskDTO task = taskSchedulerService.rejectTask(id, reason, organizationId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TASK_READ') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_QA_NURSE', 'ROLE_CLINICAL_MANAGER', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Get task by ID", description = "Retrieve task details by ID (QA reviewers and managers can view tasks for review)")
    public ResponseEntity<TaskDTO> getTaskById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        TaskDTO task = taskSchedulerService.getTaskById(id, organizationId);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TASK_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get all tasks", description = "Retrieve all tasks for the organization")
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // SYSTEM_ADMIN can access all tasks without organization context
        boolean isSystemAdmin = false;
        if (currentUser != null) {
            isSystemAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
        } else {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                isSystemAdmin = userPrincipal.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
            }
        }
        
        if (organizationId == null && !isSystemAdmin) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        
        List<TaskDTO> tasks = taskSchedulerService.getAllTasks(organizationId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my assigned tasks", description = "Retrieve tasks assigned to the current user")
    public ResponseEntity<List<TaskDTO>> getMyTasks(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<TaskDTO> tasks = taskSchedulerService.getTasksForClinician(currentUser.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/calendar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get tasks for calendar view", description = "Retrieve tasks for calendar display (alias for /my-tasks)")
    public ResponseEntity<List<TaskDTO>> getCalendarTasks(@AuthenticationPrincipal UserPrincipal currentUser) {
        // Alias for /my-tasks - returns tasks assigned to the current user
        List<TaskDTO> tasks = taskSchedulerService.getTasksForClinician(currentUser.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/clinician/{clinicianId}")
    @PreAuthorize("hasAuthority('TASK_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get tasks for clinician", description = "Retrieve tasks assigned to a specific clinician")
    public ResponseEntity<List<TaskDTO>> getTasksForClinician(@PathVariable Long clinicianId) {
        List<TaskDTO> tasks = taskSchedulerService.getTasksForClinician(clinicianId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/by-date-range")
    @PreAuthorize("hasAuthority('TASK_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get tasks by date range", description = "Retrieve tasks within a date range")
    public ResponseEntity<List<TaskDTO>> getTasksByDateRange(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // SYSTEM_ADMIN can access all tasks without organization context
        boolean isSystemAdmin = false;
        if (currentUser != null) {
            isSystemAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
        } else {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                isSystemAdmin = userPrincipal.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
            }
        }
        
        if (organizationId == null && !isSystemAdmin) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        
        List<TaskDTO> tasks = taskSchedulerService.getTasksByDateRange(organizationId, startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-tasks/by-date-range")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my tasks by date range", description = "Retrieve my assigned tasks within a date range")
    public ResponseEntity<List<TaskDTO>> getMyTasksByDateRange(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TaskDTO> tasks = taskSchedulerService.getClinicianTasksByDateRange(currentUser.getId(), startDate, endDate);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-today")
    @PreAuthorize("hasAuthority('TASK_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get tasks due today", description = "Retrieve tasks scheduled for today")
    public ResponseEntity<List<TaskDTO>> getTasksDueToday(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // SYSTEM_ADMIN can access all tasks without organization context
        boolean isSystemAdmin = false;
        if (currentUser != null) {
            isSystemAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
        } else {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                isSystemAdmin = userPrincipal.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
            }
        }
        
        if (organizationId == null && !isSystemAdmin) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        
        List<TaskDTO> tasks = taskSchedulerService.getTasksDueToday(organizationId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('TASK_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get overdue tasks", description = "Retrieve overdue tasks")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks(
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        
        // SYSTEM_ADMIN can access all tasks without organization context
        boolean isSystemAdmin = false;
        if (currentUser != null) {
            isSystemAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
        } else {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                isSystemAdmin = userPrincipal.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
            }
        }
        
        if (organizationId == null && !isSystemAdmin) {
            throw new RuntimeException("Organization ID is required. Please select an organization.");
        }
        
        List<TaskDTO> tasks = taskSchedulerService.getOverdueTasks(organizationId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('TASK_READ') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get tasks by patient", description = "Retrieve all tasks for a specific patient")
    public ResponseEntity<List<TaskDTO>> getTasksByPatient(@PathVariable Long patientId) {
        List<TaskDTO> tasks = taskSchedulerService.getTasksByPatient(patientId);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TASK_DELETE') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete task", description = "Soft delete a task")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable Long id) {
        taskSchedulerService.deleteTask(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task deleted successfully");
        return ResponseEntity.ok(response);
    }
}
