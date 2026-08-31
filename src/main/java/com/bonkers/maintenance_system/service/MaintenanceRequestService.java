package com.bonkers.maintenance_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.AssignStaffDTO;
import com.bonkers.maintenance_system.dto.CreateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.MaintenanceRequestResponseDTO;
import com.bonkers.maintenance_system.dto.StatusHistoryResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.UpdateStatusDTO;
import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Role;
import com.bonkers.maintenance_system.model.Status;
import com.bonkers.maintenance_system.model.StatusHistory;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.FacilityRepository;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;
import com.bonkers.maintenance_system.repository.StatusHistoryRepository;
import com.bonkers.maintenance_system.repository.UserRepository;

@Service
public class MaintenanceRequestService {
    private final StatusHistoryRepository statusHistoryRepository;
    private final FacilityRepository facilityRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    public MaintenanceRequestService(FacilityRepository facilityRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository,
            StatusHistoryRepository statusHistoryRepository) {
        this.facilityRepository = facilityRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    private StatusHistory logStatusHistory(Status oldStatus, Status newStatus, MaintenanceRequest maintenanceRequest,
            User user) {
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setOldStatus(oldStatus);
        statusHistory.setNewStatus(newStatus);
        statusHistory.setMaintenanceRequest(maintenanceRequest);
        statusHistory.setChangedBy(user);
        statusHistory.setChangedAt(LocalDateTime.now());

        return statusHistoryRepository.save(statusHistory);
    }

    private MaintenanceRequestResponseDTO toDto(MaintenanceRequest entity) {
        MaintenanceRequestResponseDTO dto = new MaintenanceRequestResponseDTO();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setDueAt(entity.getDueAt());
        dto.setFacilityName(entity.getFacility().getName());
        dto.setTenantName(entity.getTenant().getName());
        if (entity.getAssignedStaff() != null) {
            dto.setAssignedStaffName(entity.getAssignedStaff().getName());
        }

        return dto;
    }

    private StatusHistoryResponseDTO toDto(StatusHistory entity) {
        StatusHistoryResponseDTO dto = new StatusHistoryResponseDTO();

        dto.setId(entity.getId());
        dto.setOldStatus(entity.getOldStatus());
        dto.setNewStatus(entity.getNewStatus());
        dto.setChangedAt(entity.getChangedAt());
        dto.setMaintenanceRequest(entity.getMaintenanceRequest().getTitle());
        dto.setChangedBy(entity.getChangedBy().getName());

        return dto;
    }

    public MaintenanceRequestResponseDTO createMaintenanceRequest(CreateMaintenanceRequestDTO request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (principalName == null || principalName.isBlank()) {
            throw new RuntimeException("No authenticated tenant found");
        }

        User tenant = userRepository.findByEmail(principalName)
                .or(() -> userRepository.findByName(principalName))
                .orElseThrow(() -> new RuntimeException("Tenant not found for principal: " + principalName));

        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
        maintenanceRequest.setTitle(request.getTitle());
        maintenanceRequest.setDescription(request.getDescription());
        maintenanceRequest.setPriority(request.getPriority());
        maintenanceRequest.setFacility(facility);
        maintenanceRequest.setCreatedAt(LocalDateTime.now());
        maintenanceRequest.setTenant(tenant);

        MaintenanceRequest saved = maintenanceRequestRepository.save(maintenanceRequest);

        return toDto(saved);
    }

    public MaintenanceRequestResponseDTO updateMaintenanceRequest(Long id, UpdateMaintenanceRequestDTO request) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        maintenanceRequest.setTitle(request.getTitle());
        maintenanceRequest.setDescription(request.getDescription());
        maintenanceRequest.setPriority(request.getPriority());

        MaintenanceRequest saved = maintenanceRequestRepository.save(maintenanceRequest);
        return toDto(saved);
    }

    public MaintenanceRequestResponseDTO getMaintenanceRequest(Long id) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        return toDto(maintenanceRequest);
    }

    public void deleteMaintenanceRequest(Long id) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        maintenanceRequestRepository.delete(maintenanceRequest);
    }

    public List<MaintenanceRequestResponseDTO> getAllMaintenanceRequests() {
        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (principalName == null || principalName.isBlank()) {
            throw new RuntimeException("No authenticated tenant found");
        }

        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MaintenanceRequest> results;

        switch (user.getRole()) {
            case TENANT -> results = maintenanceRequestRepository.findByTenant(user);
            case STAFF -> results = maintenanceRequestRepository.findByAssignedStaff(user);
            case ADMIN -> results = maintenanceRequestRepository.findAll();
            default -> throw new IllegalStateException("Unknown role: " + user.getRole());
        }

        return results.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<StatusHistoryResponseDTO> getStatusHistory(Long maintenanceRequestId) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
            .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        List<StatusHistory> history = statusHistoryRepository.findByMaintenanceRequestOrderByChangedAtAsc(maintenanceRequest);

        return history.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private void validateStatusTransition(Status current, Status next) {
        boolean valid = switch (current) {
            case SUBMITTED -> next == Status.ASSIGNED;
            case ASSIGNED -> next == Status.IN_PROGRESS;
            case IN_PROGRESS -> next == Status.RESOLVED;
            case RESOLVED -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " -> " + next);
        }
    }

    public MaintenanceRequestResponseDTO updateStatus(Long id, UpdateStatusDTO request) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        Status currentStatus = maintenanceRequest.getStatus();
        Status nextStatus = request.getStatus();

        validateStatusTransition(currentStatus, nextStatus);

        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (principalName == null || principalName.isBlank()) {
            throw new RuntimeException("No authenticated user found");
        }

        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new RuntimeException("User not found for principal: " + principalName));

        maintenanceRequest.setStatus(nextStatus);
        MaintenanceRequest savedRequest = maintenanceRequestRepository.save(maintenanceRequest);

        logStatusHistory(currentStatus, nextStatus, maintenanceRequest, user);

        return toDto(savedRequest);
    }

    public MaintenanceRequestResponseDTO assignStaff(Long id, AssignStaffDTO request) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        User user = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(user.getRole() == Role.STAFF)) {
            throw new IllegalStateException("Invalid role: " + user.getRole());
        }

        maintenanceRequest.setAssignedStaff(user);

        MaintenanceRequest savedRequest = maintenanceRequestRepository.save(maintenanceRequest);

        return toDto(savedRequest);
    }
}