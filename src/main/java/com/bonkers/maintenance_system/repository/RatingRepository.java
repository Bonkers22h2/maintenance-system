package com.bonkers.maintenance_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Rating;
import com.bonkers.maintenance_system.model.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByMaintenanceRequest(MaintenanceRequest maintenanceRequest);

    List<Rating> findByMaintenanceRequest_Tenant(User tenant);

    List<Rating> findByMaintenanceRequest_AssignedStaff(User staff);
}