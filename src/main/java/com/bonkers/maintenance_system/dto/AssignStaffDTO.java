package com.bonkers.maintenance_system.dto;

import jakarta.validation.constraints.NotNull;

public class AssignStaffDTO {
    @NotNull
    private Long staffId;


    public Long getStaffId() {
        return this.staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

}