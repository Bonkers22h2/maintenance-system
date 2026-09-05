package com.bonkers.maintenance_system.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.model.MaintenanceRequest;
import com.bonkers.maintenance_system.model.RecurringTask;
import com.bonkers.maintenance_system.model.Status;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.MaintenanceRequestRepository;
import com.bonkers.maintenance_system.repository.RecurringTaskRepository;
import com.bonkers.maintenance_system.repository.UserRepository;

@Component
public class RecurringTaskJob {

    private final RecurringTaskRepository recurringTaskRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;

    public RecurringTaskJob(RecurringTaskRepository recurringTaskRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository) {
        this.recurringTaskRepository = recurringTaskRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 86400000) // once a day
    public void generateDueTasks() {
        List<RecurringTask> allTasks = recurringTaskRepository.findAll();

        // Placeholder "system" user acts as the tenant for auto-generated requests
        User systemUser = userRepository.findByEmail("system@maintenance.local")
                .orElseThrow(() -> new RuntimeException("System user not found — create one first"));

        for (RecurringTask task : allTasks) {
            int days = switch (task.getIntervalDays()) {
                case WEEKLY -> 7;
                case MONTHLY -> 30;
                case QUARTERLY -> 90;
                case SEMI_ANNUALLY -> 182;
                case ANNUALLY -> 365;
            };

            boolean isDue = task.getLastGeneratedAt() == null
                    || task.getLastGeneratedAt().plusDays(days).isBefore(LocalDateTime.now());

            if (isDue) {
                Facility facility = task.getFacility();

                MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
                maintenanceRequest.setTitle(task.getTitle());
                maintenanceRequest.setDescription(task.getDescription());
                maintenanceRequest.setPriority(task.getPriority());
                maintenanceRequest.setFacility(facility);
                maintenanceRequest.setTenant(systemUser);
                maintenanceRequest.setCreatedAt(LocalDateTime.now());
                maintenanceRequest.setStatus(Status.SUBMITTED);

                maintenanceRequestRepository.save(maintenanceRequest);

                task.setLastGeneratedAt(LocalDateTime.now());
                recurringTaskRepository.save(task);

                System.out.println("Auto-generated maintenance request for recurring task #" + task.getId());
            }
        }
    }
}