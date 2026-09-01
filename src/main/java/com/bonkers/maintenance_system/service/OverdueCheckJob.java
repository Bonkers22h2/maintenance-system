package com.bonkers.maintenance_system.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.Status;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;

@Component
public class OverdueCheckJob {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public OverdueCheckJob(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    @Scheduled(fixedRate = 3600000) // every hour
    public void checkOverdueRequests() {
        List<MaintenanceRequest> overdue = maintenanceRequestRepository
                .findByDueAtBeforeAndStatusNot(LocalDateTime.now(), Status.RESOLVED);

        for (MaintenanceRequest request : overdue) {
            System.out.println("OVERDUE: Request #" + request.getId() + " - " + request.getTitle());
        }
    }
}