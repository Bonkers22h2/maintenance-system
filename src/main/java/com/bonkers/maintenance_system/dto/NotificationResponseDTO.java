package com.bonkers.maintenance_system.dto;

import java.time.LocalDateTime;

public class NotificationResponseDTO {
    private Long id;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private String maintenanceRequest;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return this.read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMaintenanceRequest() {
        return this.maintenanceRequest;
    }

    public void setMaintenanceRequest(String maintenanceRequest) {
        this.maintenanceRequest = maintenanceRequest;
    }
}