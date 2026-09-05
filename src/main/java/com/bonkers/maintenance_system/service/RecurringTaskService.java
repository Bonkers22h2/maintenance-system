package com.bonkers.maintenance_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.CreateRecurringTasksDTO;
import com.bonkers.maintenance_system.dto.RecurringTasksResponseDTO;
import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.model.RecurringTask;
import com.bonkers.maintenance_system.repository.FacilityRepository;
import com.bonkers.maintenance_system.repository.RecurringTaskRepository;

@Service
public class RecurringTaskService {
    private final RecurringTaskRepository recurringTaskRepository;
    private final FacilityRepository facilityRepository;

    public RecurringTaskService(RecurringTaskRepository recurringTaskRepository,
            FacilityRepository facilityRepository) {
        this.recurringTaskRepository = recurringTaskRepository;
        this.facilityRepository = facilityRepository;
    }

    private RecurringTasksResponseDTO toDto(RecurringTask entity) {
        RecurringTasksResponseDTO dto = new RecurringTasksResponseDTO();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setFacilityName(entity.getFacility().getName());
        dto.setLastGeneratedAt(entity.getLastGeneratedAt());
        dto.setPriority(entity.getPriority());
        dto.setIntervalDays(entity.getIntervalDays().toString());

        return dto;
    }

    public RecurringTasksResponseDTO createRecurringTask(CreateRecurringTasksDTO request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        RecurringTask recurringTask = new RecurringTask();
        recurringTask.setTitle(request.getTitle());
        recurringTask.setDescription(request.getDescription());
        recurringTask.setFacility(facility);
        recurringTask.setPriority(request.getPriority());
        recurringTask.setIntervalDays(request.getIntervalDays());

        RecurringTask saved = recurringTaskRepository.save(recurringTask);
        return toDto(saved);
    }

    public Page<RecurringTasksResponseDTO> getAllRecurringTask(Pageable pageable) {
        Page<RecurringTask> recurringTask = recurringTaskRepository.findAll(pageable);
        return recurringTask.map(this::toDto);
    }

}