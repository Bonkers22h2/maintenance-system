package com.bonkers.maintenance_system.service;


import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.CreateFacilityDTO;
import com.bonkers.maintenance_system.dto.FacilityResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateFacilityDTO;
import com.bonkers.maintenance_system.exception.ResourceNotFoundException;
import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.repository.FacilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;

    // Constructor to initialize FacilityRepository
    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    // Convert Facility entity to DTO
    private FacilityResponseDTO toDto(Facility entity) {
        FacilityResponseDTO dto = new FacilityResponseDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        dto.setFacilityType(entity.getFacilityType());

        return dto;
    }

    // Create a new facility
    public FacilityResponseDTO createFacility(CreateFacilityDTO request) {
        Facility facility = new Facility();
        facility.setName(request.getName());
        facility.setLocation(request.getLocation());
        facility.setFacilityType(request.getFacilityType());

        Facility saved = facilityRepository.save(facility);

        return toDto(saved);
    }

    // Retrieve a specific facility by ID
    public FacilityResponseDTO getFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        return toDto(facility);
    }

    // Retrieve all facilities
    public Page<FacilityResponseDTO> getAllFacility(Pageable pageable) {
        Page<Facility> all = facilityRepository.findAll(pageable);
        return all.map(this::toDto);
    }

    // Update an existing facility
    public FacilityResponseDTO updateFacility(Long id, UpdateFacilityDTO request) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        facility.setName(request.getName());
        facility.setLocation(request.getLocation());
        facility.setFacilityType(request.getFacilityType());

        Facility saved = facilityRepository.save(facility);
        return toDto(saved);
    }

    // Delete a facility
    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        facilityRepository.delete(facility);
    }
}