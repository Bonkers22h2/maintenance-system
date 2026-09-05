package com.bonkers.maintenance_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.NotificationResponseDTO;
import com.bonkers.maintenance_system.exception.UnathorizedExceptionHandler;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Notification;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.NotificationRepository;
import com.bonkers.maintenance_system.repository.UserRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private NotificationResponseDTO toDto(Notification entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();

        dto.setId(entity.getId());
        dto.setMessage(entity.getMessage());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setRead(entity.isRead());
        if (entity.getMaintenanceRequest() != null) {
            dto.setMaintenanceRequest(entity.getMaintenanceRequest().getTitle());
        }

        return dto;
    }

    public void createNotification(User user, String message, MaintenanceRequest maintenanceRequest) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setMaintenanceRequest(maintenanceRequest);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public List<NotificationResponseDTO> getMyNotifications() {
        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if(principalName == null || principalName.isBlank()) {
            throw new UnathorizedExceptionHandler("No authenticated user found");
        }

        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications = notificationRepository.findByUser(user);

        return notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Mark one of the current user's notifications as read
    public NotificationResponseDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        String principalName = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        User user = userRepository.findByEmail(principalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ownership check — can only mark your own notifications as read
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);

        return toDto(saved);
    }
}