package com.bonkers.maintenance_system.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonkers.maintenance_system.dto.CreateFacilityDTO;
import com.bonkers.maintenance_system.dto.FacilityResponseDTO;
import com.bonkers.maintenance_system.dto.UpdateFacilityDTO;
import com.bonkers.maintenance_system.service.FacilityService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/facilities")
public class FacilityController {
    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    public ResponseEntity<List<FacilityResponseDTO>> getAllFacility() {
        List<FacilityResponseDTO> facilities = facilityService.getAllFacility();
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityResponseDTO> getFacility(@PathVariable Long id) {
        FacilityResponseDTO facility = facilityService.getFacility(id);
        return ResponseEntity.ok(facility);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacilityResponseDTO> updateFacility(@PathVariable Long id, @Valid @RequestBody UpdateFacilityDTO request){
        FacilityResponseDTO facility = facilityService.updateFacility(id, request);
        return ResponseEntity.ok(facility);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FacilityResponseDTO> deleteFacility(@PathVariable Long id){
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<FacilityResponseDTO> createFacility(@Valid @RequestBody CreateFacilityDTO request) {
        FacilityResponseDTO facility = facilityService.createFacility(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(facility);
    }
    
}