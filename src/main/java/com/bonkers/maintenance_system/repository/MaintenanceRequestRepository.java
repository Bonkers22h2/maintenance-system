package com.bonkers.maintenance_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.User;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    Optional<MaintenanceRequest> findById(Long id);

    List<MaintenanceRequest> findByTenant(User tenant);

    List<MaintenanceRequest> findByAssignedStaff(User staff);
}