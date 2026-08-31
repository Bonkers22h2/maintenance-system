package com.bonkers.maintenance_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonkers.maintenance_system.model.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long>{
    Optional<Facility> findById(Long id);
}