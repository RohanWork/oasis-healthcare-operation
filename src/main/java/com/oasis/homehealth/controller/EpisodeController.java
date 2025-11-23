package com.oasis.homehealth.controller;

import com.oasis.homehealth.dto.EpisodeDTO;
import com.oasis.homehealth.dto.EpisodeRequest;
import com.oasis.homehealth.service.EpisodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/episodes")
@Tag(name = "Episode Management", description = "APIs for managing episodes of care")
public class EpisodeController {

    @Autowired
    private EpisodeService episodeService;

    @PostMapping
    @PreAuthorize("hasAuthority('EPISODE_CREATE')")
    @Operation(summary = "Create new episode", description = "Create a new episode of care")
    public ResponseEntity<EpisodeDTO> createEpisode(
            @Valid @RequestBody EpisodeRequest request,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        EpisodeDTO episode = episodeService.createEpisode(request, organizationId);
        return new ResponseEntity<>(episode, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EPISODE_UPDATE')")
    @Operation(summary = "Update episode", description = "Update an existing episode")
    public ResponseEntity<EpisodeDTO> updateEpisode(
            @PathVariable Long id,
            @Valid @RequestBody EpisodeRequest request) {
        EpisodeDTO episode = episodeService.updateEpisode(id, request);
        return ResponseEntity.ok(episode);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EPISODE_READ')")
    @Operation(summary = "Get episode by ID", description = "Retrieve episode details by ID")
    public ResponseEntity<EpisodeDTO> getEpisodeById(@PathVariable Long id) {
        EpisodeDTO episode = episodeService.getEpisodeById(id);
        return ResponseEntity.ok(episode);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('EPISODE_READ')")
    @Operation(summary = "Get episodes by patient", description = "Retrieve all episodes for a patient")
    public ResponseEntity<List<EpisodeDTO>> getEpisodesByPatient(@PathVariable Long patientId) {
        List<EpisodeDTO> episodes = episodeService.getEpisodesByPatient(patientId);
        return ResponseEntity.ok(episodes);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EPISODE_READ')")
    @Operation(summary = "Get all episodes", description = "Retrieve all episodes for the organization")
    public ResponseEntity<List<EpisodeDTO>> getAllEpisodes(HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<EpisodeDTO> episodes = episodeService.getEpisodesByOrganization(organizationId);
        return ResponseEntity.ok(episodes);
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAuthority('EPISODE_READ')")
    @Operation(summary = "Get expiring episodes", description = "Retrieve episodes expiring within specified days")
    public ResponseEntity<List<EpisodeDTO>> getExpiringEpisodes(
            @RequestParam(defaultValue = "14") Integer daysAhead,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        List<EpisodeDTO> episodes = episodeService.getExpiringEpisodes(organizationId, daysAhead);
        return ResponseEntity.ok(episodes);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EPISODE_DELETE')")
    @Operation(summary = "Delete episode", description = "Soft delete an episode")
    public ResponseEntity<Map<String, String>> deleteEpisode(@PathVariable Long id) {
        episodeService.deleteEpisode(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Episode deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/recertify")
    @PreAuthorize("hasAuthority('EPISODE_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Recertify episode", description = "Create a new recertification episode")
    public ResponseEntity<EpisodeDTO> recertifyEpisode(
            @PathVariable Long id,
            @RequestParam LocalDate newCertificationStartDate,
            @RequestParam(defaultValue = "60") Integer certificationPeriod,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        EpisodeDTO newEpisode = episodeService.recertifyEpisode(id, newCertificationStartDate, certificationPeriod, organizationId);
        return ResponseEntity.ok(newEpisode);
    }

    @PostMapping("/{id}/discharge")
    @PreAuthorize("hasAuthority('EPISODE_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN', 'ROLE_CLINICAL_MANAGER')")
    @Operation(summary = "Discharge episode", description = "Discharge an active episode")
    public ResponseEntity<EpisodeDTO> dischargeEpisode(
            @PathVariable Long id,
            @RequestParam String dischargeReason,
            @RequestParam String dischargeDisposition,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        EpisodeDTO episode = episodeService.dischargeEpisode(id, dischargeReason, dischargeDisposition, organizationId);
        return ResponseEntity.ok(episode);
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('EPISODE_UPDATE') or hasAnyRole('ROLE_SYSTEM_ADMIN', 'ROLE_ORG_ADMIN')")
    @Operation(summary = "Archive episode", description = "Archive a completed or cancelled episode")
    public ResponseEntity<EpisodeDTO> archiveEpisode(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long organizationId = (Long) httpRequest.getAttribute("organizationId");
        EpisodeDTO episode = episodeService.archiveEpisode(id, organizationId);
        return ResponseEntity.ok(episode);
    }
}

