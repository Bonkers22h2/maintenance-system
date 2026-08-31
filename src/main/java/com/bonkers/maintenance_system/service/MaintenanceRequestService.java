package com.bonkers.maintenance_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.CreateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.MaintenanceRequestResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.UpdateStatusDTO;
import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Status;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.FacilityRepository;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;
import com.bonkers.maintenance_system.repository.UserRepository;

@Service
public class MaintenanceRequestService {
    private final FacilityRepository facilityRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    public MaintenanceRequestService(FacilityRepository facilityRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository) {
        this.facilityRepository = facilityRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
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

        return dto;
    }

    public MaintenanceRequestResponseDTO createMaintenanceRequest(CreateMaintenanceRequestDTO request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        User tenant = userRepository.findById(1L) // replace 1L with your real placeholder user's id
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

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
        List<MaintenanceRequest> all = maintenanceRequestRepository.findAll();
        return all.stream()
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

        maintenanceRequest.setStatus(nextStatus);

        MaintenanceRequest savedRequest = maintenanceRequestRepository.save(maintenanceRequest);

        return toDto(savedRequest);
    }

}