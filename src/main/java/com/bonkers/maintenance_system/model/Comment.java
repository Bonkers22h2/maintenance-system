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
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "maintenance_request_id", nullable = false)
    private MaintenanceRequest maintenanceRequest;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MaintenanceRequest getMaintenanceRequest() {
        return this.maintenanceRequest;
    }

    public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
        this.maintenanceRequest = maintenanceRequest;
    }

}