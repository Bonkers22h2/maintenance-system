package com.bonkers.maintenance_system.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonkers.maintenance_system.dto.AssignStaffDTO;
import com.bonkers.maintenance_system.dto.AttachmentResponseDTO;
import com.bonkers.maintenance_system.dto.CreateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.MaintenanceRequestResponseDTO;
import com.bonkers.maintenance_system.dto.StatusHistoryResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateMaintenanceRequestDTO;
import com.bonkers.maintenance_system.dto.UpdateStatusDTO;
import com.bonkers.maintenance_system.model.Attachment;
import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Role;
import com.bonkers.maintenance_system.model.Status;
import com.bonkers.maintenance_system.model.StatusHistory;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.AttachmentRepository;
import com.bonkers.maintenance_system.repository.FacilityRepository;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;
import com.bonkers.maintenance_system.repository.StatusHistoryRepository;
import com.bonkers.maintenance_system.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;

@Service
public class MaintenanceRequestService {
    private final StatusHistoryRepository statusHistoryRepository;
    private final FacilityRepository facilityRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    // Constructor to initialize repositories
    private final AttachmentRepository attachmentRepository;

    public MaintenanceRequestService(FacilityRepository facilityRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository,
            StatusHistoryRepository statusHistoryRepository,
            AttachmentRepository attachmentRepository) {
        this.facilityRepository = facilityRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.attachmentRepository = attachmentRepository;
    }

    // Log status change history for a maintenance request
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

    // Convert MaintenanceRequest entity to DTO
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
        boolean isOverdue = entity.getDueAt() != null
            && entity.getDueAt().isBefore(LocalDateTime.now())
            && entity.getStatus() != Status.RESOLVED;
        dto.setOverdue(isOverdue);

        return dto;
    }

    private AttachmentResponseDTO toDto(Attachment entity) {
        AttachmentResponseDTO dto = new AttachmentResponseDTO();

        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setMaintenanceRequestId(entity.getMaintenanceRequest().getId());
        dto.setUploadedAt(entity.getUploadedAt());

        return dto;
    }

    // Convert StatusHistory entity to DTO
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

    // Create a new maintenance request
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

        int hours = switch (request.getPriority()) {
            case HIGH -> 24;
            case MEDIUM -> 72;
            case LOW -> 168;
        };

        LocalDateTime dueAt = LocalDateTime.now().plusHours(hours);

        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
        maintenanceRequest.setTitle(request.getTitle());
        maintenanceRequest.setDescription(request.getDescription());
        maintenanceRequest.setPriority(request.getPriority());
        maintenanceRequest.setFacility(facility);
        maintenanceRequest.setCreatedAt(LocalDateTime.now());
        maintenanceRequest.setTenant(tenant);
        maintenanceRequest.setDueAt(dueAt);

        MaintenanceRequest saved = maintenanceRequestRepository.save(maintenanceRequest);

        return toDto(saved);
    }

    // Update an existing maintenance request
    public MaintenanceRequestResponseDTO updateMaintenanceRequest(Long id, UpdateMaintenanceRequestDTO request) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        maintenanceRequest.setTitle(request.getTitle());
        maintenanceRequest.setDescription(request.getDescription());
        maintenanceRequest.setPriority(request.getPriority());

        MaintenanceRequest saved = maintenanceRequestRepository.save(maintenanceRequest);
        return toDto(saved);
    }

    // Retrieve a specific maintenance request by ID
    public MaintenanceRequestResponseDTO getMaintenanceRequest(Long id) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        return toDto(maintenanceRequest);
    }

    // Delete a maintenance request
    public void deleteMaintenanceRequest(Long id) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        maintenanceRequestRepository.delete(maintenanceRequest);
    }

    // Retrieve all maintenance requests filtered by user role
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

    // Retrieve status history for a maintenance request
    public List<StatusHistoryResponseDTO> getStatusHistory(Long maintenanceRequestId) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        List<StatusHistory> history = statusHistoryRepository
                .findByMaintenanceRequestOrderByChangedAtAsc(maintenanceRequest);

        return history.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Validate if status transition is allowed
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

    // Update the status of a maintenance request
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

    // Assign staff member to a maintenance request
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    public AttachmentResponseDTO uploadAttachment(Long maintenanceRequestId, MultipartFile file) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = Paths.get(uploadDir, uniqueFileName);

        try {
            Files.write(targetPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFilePath(targetPath.toString());
        attachment.setUploadedAt(LocalDateTime.now());
        attachment.setMaintenanceRequest(maintenanceRequest);

        Attachment saved = attachmentRepository.save(attachment);

        AttachmentResponseDTO dto = new AttachmentResponseDTO();
        dto.setId(saved.getId());
        dto.setFileName(saved.getFileName());
        dto.setUploadedAt(saved.getUploadedAt());
        dto.setMaintenanceRequestId(saved.getMaintenanceRequest().getId());

        return dto;
    }

    public List<AttachmentResponseDTO> getAttachments(Long maintenanceRequestId) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        List<Attachment> attachments = attachmentRepository.findByMaintenanceRequest(maintenanceRequest);

        return attachments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Attachment getAttachmentEntity(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
    }

}