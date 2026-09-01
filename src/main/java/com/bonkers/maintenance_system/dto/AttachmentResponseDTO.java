package com.bonkers.maintenance_system.dto;

import java.time.LocalDateTime;

public class AttachmentResponseDTO {
    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
    private Long maintenanceRequestId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getUploadedAt() {
        return this.uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getMaintenanceRequestId() {
        return this.maintenanceRequestId;
    }

    public void setMaintenanceRequestId(Long maintenanceRequestId) {
        this.maintenanceRequestId = maintenanceRequestId;
    }
}