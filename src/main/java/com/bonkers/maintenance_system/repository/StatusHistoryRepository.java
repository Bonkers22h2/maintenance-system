package com.bonkers.maintenance_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.StatusHistory;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByMaintenanceRequestOrderByChangedAtAsc(MaintenanceRequest maintenanceRequest);
}