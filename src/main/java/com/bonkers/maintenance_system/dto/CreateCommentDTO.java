package com.bonkers.maintenance_system.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCommentDTO {
    @NotBlank
    private String content;


    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}