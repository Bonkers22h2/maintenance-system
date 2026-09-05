package com.bonkers.maintenance_system.dto;

import java.time.LocalDateTime;

import com.bonkers.maintenance_system.model.IntervalDays;
import com.bonkers.maintenance_system.model.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRecurringTasksDTO {

    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Long facilityId;
    @NotNull
    private IntervalDays intervalDays;
    @NotNull
    private Priority priority;

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

    public Long getFacilityId() {
        return this.facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public IntervalDays getIntervalDays() {
        return this.intervalDays;
    }

    public void setIntervalDays(IntervalDays intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

}