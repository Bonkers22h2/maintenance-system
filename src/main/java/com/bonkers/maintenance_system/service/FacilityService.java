package com.bonkers.maintenance_system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.CreateFacilityDTO;
import com.bonkers.maintenance_system.dto.FacilityResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateFacilityDTO;
import com.bonkers.maintenance_system.model.Facility;
import com.bonkers.maintenance_system.repository.FacilityRepository;

@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository){
        this.facilityRepository = facilityRepository;
    }

    private FacilityResponseDTO toDto(Facility entity) {
        FacilityResponseDTO dto = new FacilityResponseDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        dto.setFacilityType(entity.getFacilityType());

        return dto;
    }

    public FacilityResponseDTO createFacility(CreateFacilityDTO request){
        Facility facility = new Facility();
        facility.setName(request.getName());
        facility.setLocation(request.getLocation());
        facility.setFacilityType(request.getFacilityType());

        Facility saved = facilityRepository.save(facility);

        return toDto(saved);
    }

    public FacilityResponseDTO getFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        return toDto(facility);
    }

    public List<FacilityResponseDTO> getAllFacility() {
        List<Facility> all = facilityRepository.findAll();
        return all.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public FacilityResponseDTO updateFacility(Long id, UpdateFacilityDTO request){
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        facility.setName(request.getName());
        facility.setLocation(request.getLocation());
        facility.setFacilityType(request.getFacilityType());

        Facility saved = facilityRepository.save(facility);
        return toDto(saved);
    }

    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
            facilityRepository.delete(facility);
    }
}