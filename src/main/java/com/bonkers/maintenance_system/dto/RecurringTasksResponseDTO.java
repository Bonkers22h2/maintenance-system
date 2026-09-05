package com.bonkers.maintenance_system.dto;

import java.time.LocalDateTime;

import com.bonkers.maintenance_system.model.Priority;

public class RecurringTasksResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String facilityName;
    private String intervalDays;
    private LocalDateTime lastGeneratedAt;
    private Priority priority;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacilityName() {
        return this.facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getIntervalDays() {
        return this.intervalDays;
    }

    public void setIntervalDays(String intervalDays) {
        this.intervalDays = intervalDays;
    }

    public LocalDateTime getLastGeneratedAt() {
        return this.lastGeneratedAt;
    }

    public void setLastGeneratedAt(LocalDateTime lastGeneratedAt) {
        this.lastGeneratedAt = lastGeneratedAt;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

}