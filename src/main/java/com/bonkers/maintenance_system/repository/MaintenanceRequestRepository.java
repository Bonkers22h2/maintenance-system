package com.bonkers.maintenance_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.MaintenanceRequest;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long>{
    Optional<MaintenanceRequest> findById(Long id);
}