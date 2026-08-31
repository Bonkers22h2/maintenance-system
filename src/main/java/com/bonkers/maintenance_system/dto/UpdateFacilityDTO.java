package com.bonkers.maintenance_system.dto;

import com.bonkers.maintenance_system.model.FacilityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateFacilityDTO {
    
    @NotBlank
    private String name;
    @NotBlank
    private String Location;
    @NotNull
    private FacilityType facilityType;


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public FacilityType getFacilityType() {
        return this.facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

}