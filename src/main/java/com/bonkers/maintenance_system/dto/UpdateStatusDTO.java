package com.bonkers.maintenance_system.dto;

import com.bonkers.maintenance_system.model.Status;

import jakarta.validation.constraints.NotNull;

public class UpdateStatusDTO {
   
    @NotNull
    private Status status;


    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}