package com.bonkers.maintenance_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.CreateRatingDTO;
import com.bonkers.maintenance_system.dto.RatingResponseDTO;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Rating;
import com.bonkers.maintenance_system.model.Status;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;
import com.bonkers.maintenance_system.repository.RatingRepository;
import com.bonkers.maintenance_system.repository.UserRepository;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
    }

    private RatingResponseDTO toDto(Rating entity) {
        RatingResponseDTO dto = new RatingResponseDTO();

        dto.setId(entity.getId());
        dto.setScore(entity.getScore());
        dto.setComment(entity.getComment());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getMaintenanceRequest() != null) {
            dto.setMaintenanceRequestTitle(entity.getMaintenanceRequest().getTitle());
        }

        return dto;
    }

    public RatingResponseDTO createRating(Long maintenanceRequestId, CreateRatingDTO request) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (principalName == null || principalName.isBlank()) {
            throw new RuntimeException("No authenticated tenant found");
        }

        User tenant = userRepository.findByEmail(principalName)
                .or(() -> userRepository.findByName(principalName))
                .orElseThrow(() -> new RuntimeException("Tenant not found for principal: " + principalName));

        if (!maintenanceRequest.getTenant().getId().equals(tenant.getId())) {
            throw new RuntimeException("Access denied");
        }
        if (maintenanceRequest.getStatus() != Status.RESOLVED) {
            throw new RuntimeException("Status not resolved");
        }

        if (ratingRepository.existsByMaintenanceRequest(maintenanceRequest)) {
            throw new RuntimeException("Maintenance request already rated");
        }

        Rating rating = new Rating();
        rating.setComment(request.getComment());
        rating.setCreatedAt(LocalDateTime.now());
        rating.setMaintenanceRequest(maintenanceRequest);
        rating.setScore(request.getScore());

        Rating saved = ratingRepository.save(rating);

        return toDto(saved);
    }

    public List<RatingResponseDTO> getAllRatings() {
        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (principalName == null || principalName.isBlank()) {
            throw new RuntimeException("No authenticated tenant found");
        }

        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Rating> results;

        switch (user.getRole()) {
            case TENANT -> results = ratingRepository.findByMaintenanceRequest_Tenant(user);
            case STAFF -> results = ratingRepository.findByMaintenanceRequest_AssignedStaff(user);
            case ADMIN -> results = ratingRepository.findAll();
            default -> throw new IllegalStateException("Unknown role: " + user.getRole());
        }

        return results.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}