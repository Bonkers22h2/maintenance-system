package com.bonkers.maintenance_system.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "recurring_tasks")
public class RecurringTask {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    
    @ManyToOne 
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
    private IntervalDays intervalDays;
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

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public IntervalDays getIntervalDays() {
        return this.intervalDays;
    }

    public void setIntervalDays(IntervalDays intervalDays) {
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