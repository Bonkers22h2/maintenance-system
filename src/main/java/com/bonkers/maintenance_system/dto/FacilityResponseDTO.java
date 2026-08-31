package com.bonkers.maintenance_system.dto;

import com.bonkers.maintenance_system.model.FacilityType;


public class FacilityResponseDTO {
    
    private Long id;
    private String name;
    private String Location;
    private FacilityType facilityType;


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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