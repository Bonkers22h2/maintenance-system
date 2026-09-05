package com.bonkers.maintenance_system.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonkers.maintenance_system.dto.CreateRecurringTasksDTO;
import com.bonkers.maintenance_system.dto.RecurringTasksResponseDTO;
import com.bonkers.maintenance_system.service.RecurringTaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recurring-tasks")
public class RecurringTaskController {
    private final RecurringTaskService recurringTaskService;

    public RecurringTaskController(RecurringTaskService recurringTaskService) {
        this.recurringTaskService = recurringTaskService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RecurringTasksResponseDTO> createRecurringTask(
            @Valid @RequestBody CreateRecurringTasksDTO request) {
        RecurringTasksResponseDTO task = recurringTaskService.createRecurringTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<RecurringTasksResponseDTO>> getAllRecurringTasks(Pageable pageable) {
        Page<RecurringTasksResponseDTO> tasks = recurringTaskService.getAllRecurringTask(pageable);
        return ResponseEntity.ok(tasks);
    }
}