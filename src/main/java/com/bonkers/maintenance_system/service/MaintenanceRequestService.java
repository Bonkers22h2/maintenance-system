package com.bonkers.maintenance_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.CreateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.MaintenanceRequestResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.repository.FacilityRepository;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;

@Service
public class MaintenanceRequestService {
    private final FacilityRepository facilityRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequestService(FacilityRepository facilityRepository,
            MaintenanceRequestRepository maintenanceRequestRepository) {
        this.facilityRepository = facilityRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
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

        return dto;
    }

    public MaintenanceRequestResponseDTO createMaintenanceRequest(CreateMaintenanceRequestDTO request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
        maintenanceRequest.setTitle(request.getTitle());
        maintenanceRequest.setDescription(request.getDescription());
        maintenanceRequest.setPriority(request.getPriority());
        maintenanceRequest.setFacility(facility);
        maintenanceRequest.setCreatedAt(LocalDateTime.now());

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
}