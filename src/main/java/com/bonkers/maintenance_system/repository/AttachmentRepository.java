package com.bonkers.maintenance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.Attachment;
import com.bonkers.maintenance_system.model.MaintenanceRequest;

import java.util.List;


public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByMaintenanceRequest(MaintenanceRequest maintenanceRequest);

}