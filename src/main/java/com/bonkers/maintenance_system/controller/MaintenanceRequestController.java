package com.bonkers.maintenance_system.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.bonkers.maintenance_system.dto.AssignStaffDTO;
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

    public MaintenanceRequestController(MaintenanceRequestService maintenanceRequestService) {
        this.maintenanceRequestService = maintenanceRequestService;
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceRequestResponseDTO>> getAllMaintenanceRequests() {
        List<MaintenanceRequestResponseDTO> maintenanceRequests = maintenanceRequestService.getAllMaintenanceRequests();
        return ResponseEntity.ok(maintenanceRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponseDTO> getMaintenanceRequest(@PathVariable Long id) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.getMaintenanceRequest(id);
        return ResponseEntity.ok(maintenanceRequest);
    }

    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponseDTO> updateMaintenanceRequest(@PathVariable Long id,
            @Valid @RequestBody UpdateMaintenanceRequestDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.updateMaintenanceRequest(id,
                request);
        return ResponseEntity.ok(maintenanceRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenanceRequest(@PathVariable Long id) {
        maintenanceRequestService.deleteMaintenanceRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<MaintenanceRequestResponseDTO> createMaintenance(
            @Valid @RequestBody CreateMaintenanceRequestDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.createMaintenanceRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceRequest);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<MaintenanceRequestResponseDTO> updateStatus(@PathVariable Long id,
            @Valid @RequestBody UpdateStatusDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.updateStatus(id, request);
        return ResponseEntity.ok(maintenanceRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<MaintenanceRequestResponseDTO> assignStaff(@PathVariable Long id,
            @Valid @RequestBody AssignStaffDTO request) {
        MaintenanceRequestResponseDTO maintenanceRequest = maintenanceRequestService.assignStaff(id, request);
        return ResponseEntity.ok(maintenanceRequest);
    }

    @PreAuthorize("hasAnyRole('TENANT', 'STAFF', 'ADMIN')")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusHistoryResponseDTO>> getStatusHistory(@PathVariable Long id) {
        List<StatusHistoryResponseDTO> history = maintenanceRequestService.getStatusHistory(id);
        return ResponseEntity.ok(history);
    }
}