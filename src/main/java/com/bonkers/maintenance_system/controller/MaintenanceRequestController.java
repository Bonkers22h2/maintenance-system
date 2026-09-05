package com.bonkers.maintenance_system.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import com.bonkers.maintenance_system.model.Attachment;
import java.io.IOException;
import java.nio.file.Path;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bonkers.maintenance_system.dto.AssignStaffDTO;
import com.bonkers.maintenance_system.dto.AttachmentResponseDTO;
import com.bonkers.maintenance_system.dto.CreateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.MaintenanceRequestResponseDTO;
import com.bonkers.maintenance_system.dto.StatusHistoryResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.UpdateStatusDTO;
import com.bonkers.maintenance_system.service.MaintenanceRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maintenance-requests")
public class MaintenanceRequestController {
    private final MaintenanceRequestService maintenanceRequestService;

    // Constructor to initialize MaintenanceRequestService
    public MaintenanceRequestController(MaintenanceRequestService maintenanceRequestService) {
        this.maintenanceRequestService = maintenanceRequestService;
    }

    // Retrieve all maintenance requests
    @GetMapping
    public ResponseEntity<List<MaintenanceRequestResponseDTO>> getAllMaintenanceRequests() {
        List<MaintenanceRequestResponseDTO> maintenanceRequests = maintenanceRequestService.getAllMaintenanceRequests();
        return ResponseEntity.ok(maintenanceRequests);
    }

    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<MaintenanceRequestResponseDTO>> getAllMaintenanceRequestsByFacility(@PathVariable Long facilityId) {
        List<MaintenanceRequestResponseDTO> maintenaceRequests = maintenanceRequestService.getMaintenanceRequestFromFacility(facilityId);
        return ResponseEntity.ok(maintenaceRequests);
    }

    // Retrieve a specific maintenance request by ID
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponseDTO> getMaintenanceRequest(@PathVariable Long id) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.getMaintenanceRequest(id);
        return ResponseEntity.ok(maintenanceRequest);
    }

    // Update an existing maintenance request (Tenant and Admin only)
    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponseDTO> updateMaintenanceRequest(@PathVariable Long id,
            @Valid @RequestBody UpdateMaintenanceRequestDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.updateMaintenanceRequest(id,
                request);
        return ResponseEntity.ok(maintenanceRequest);
    }

    // Delete a maintenance request (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenanceRequest(@PathVariable Long id) {
        maintenanceRequestService.deleteMaintenanceRequest(id);
        return ResponseEntity.noContent().build();
    }

    // Create a new maintenance request (Tenant and Admin only)
    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<MaintenanceRequestResponseDTO> createMaintenance(
            @Valid @RequestBody CreateMaintenanceRequestDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.createMaintenanceRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceRequest);
    }

    // Update the status of a maintenance request (Staff and Admin only)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<MaintenanceRequestResponseDTO> updateStatus(@PathVariable Long id,
            @Valid @RequestBody UpdateStatusDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.updateStatus(id, request);
        return ResponseEntity.ok(maintenanceRequest);
    }

    // Assign staff to a maintenance request (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<MaintenanceRequestResponseDTO> assignStaff(@PathVariable Long id,
            @Valid @RequestBody AssignStaffDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.assignStaff(id, request);
        return ResponseEntity.ok(maintenanceRequest);
    }

    // Retrieve status history for a maintenance request
    @PreAuthorize("hasAnyRole('TENANT', 'STAFF', 'ADMIN')")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusHistoryResponseDTO>> getStatusHistory(@PathVariable Long id) {
        List<StatusHistoryResponseDTO> history = maintenanceRequestService.getStatusHistory(id);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<AttachmentResponseDTO> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        AttachmentResponseDTO attachment = maintenanceRequestService.uploadAttachment(id, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponseDTO>> getAttachments(@PathVariable Long id) {
        List<AttachmentResponseDTO> attachments = maintenanceRequestService.getAttachments(id);
        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/{id}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long id,
            @PathVariable Long attachmentId) throws IOException {

        Attachment attachment = maintenanceRequestService.getAttachmentEntity(attachmentId);

        Path filePath = Paths.get(attachment.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found on disk");
        }

        String contentType = Files.probeContentType(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }
}