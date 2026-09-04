package com.bonkers.maintenance_system.dto;

import java.time.LocalDateTime;

public class RatingResponseDTO {
    private Long id;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
    private String maintenanceRequestTitle;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return this.score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMaintenanceRequestTitle() {
        return this.maintenanceRequestTitle;
    }

    public void setMaintenanceRequestTitle(String maintenanceRequestTitle) {
        this.maintenanceRequestTitle = maintenanceRequestTitle;
    }

}