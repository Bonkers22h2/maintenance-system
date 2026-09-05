package com.bonkers.maintenance_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.RecurringTask;

public interface RecurringTaskRepository extends JpaRepository<RecurringTask, Long>{
}