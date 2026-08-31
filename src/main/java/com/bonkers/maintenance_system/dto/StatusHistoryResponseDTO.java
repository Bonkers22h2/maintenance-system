package com.bonkers.maintenance_system.dto;

import java.time.LocalDateTime;

import com.bonkers.maintenance_system.model.Status;

public class StatusHistoryResponseDTO {
    private Long id;
    private Status oldStatus;
    private Status newStatus;
    private LocalDateTime changedAt;
    private String maintenanceRequest;
    private String changedBy;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getOldStatus() {
        return this.oldStatus;
    }

    public void setOldStatus(Status oldStatus) {
        this.oldStatus = oldStatus;
    }

    public Status getNewStatus() {
        return this.newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return this.changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getMaintenanceRequest() {
        return this.maintenanceRequest;
    }

    public void setMaintenanceRequest(String maintenanceRequest) {
        this.maintenanceRequest = maintenanceRequest;
    }

    public String getChangedBy() {
        return this.changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}