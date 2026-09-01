package com.bonkers.maintenance_system.service;

import com.bonkers.maintenance_system.controller.MaintenanceRequestController;
import com.bonkers.maintenance_system.repository.StatusHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.CommentResponseDTO;
import com.bonkers.maintenance_system.dto.CreateCommentDTO;
import com.bonkers.maintenance_system.model.Comment;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Role;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.CommentRepository;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;
import com.bonkers.maintenance_system.repository.UserRepository;

@Service
public class CommentService {
    private final StatusHistoryRepository statusHistoryRepository;
    private final CommentRepository commentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
            MaintenanceRequestRepository maintenanceRequestRepository, UserRepository userRepository,
            StatusHistoryRepository statusHistoryRepository) {
        this.commentRepository = commentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    private CommentResponseDTO toDto(Comment entity) {
        CommentResponseDTO dto = new CommentResponseDTO();

        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setAuthorName(entity.getAuthor().getName());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public CommentResponseDTO createComment(Long maintenanceRequestId, CreateCommentDTO request) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isTenant = maintenanceRequest.getTenant().getId().equals(user.getId());
        boolean isAssignedStaff = maintenanceRequest.getAssignedStaff() != null
                && maintenanceRequest.getAssignedStaff().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isTenant && !isAssignedStaff && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setMaintenanceRequest(maintenanceRequest);
        comment.setAuthor(user);

        Comment saved = commentRepository.save(comment);

        return toDto(saved);
    }

    public List<CommentResponseDTO> getComment(Long maintenanceRequestId) {
        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new RuntimeException("Maintenance Request not found"));

        // Get the email of whoever is currently logged in (from the JWT)
        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        // Look up the actual User record for that logged-in person
        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ownership check — is this user allowed to view this request's history?
        boolean isOwner = maintenanceRequest.getTenant().getId().equals(user.getId());
        boolean isAssignedStaff = maintenanceRequest.getAssignedStaff() != null
                && maintenanceRequest.getAssignedStaff().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        // Block access if they're not the tenant, not assigned staff, and not an
        // admin
        if (!isOwner && !isAssignedStaff && !isAdmin) {
            throw new RuntimeException("Access denied");
        }

        List<Comment> comment = commentRepository.findByMaintenanceRequest(maintenanceRequest);

        return comment.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}