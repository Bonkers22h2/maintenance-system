package com.bonkers.maintenance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.Comment;
import com.bonkers.maintenance_system.model.MaintenanceRequest;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMaintenanceRequest(MaintenanceRequest maintenanceRequest);
    
}