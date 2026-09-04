package com.bonkers.maintenance_system.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonkers.maintenance_system.dto.CreateRatingDTO;
import com.bonkers.maintenance_system.dto.RatingResponseDTO;
import com.bonkers.maintenance_system.service.RatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maintenance-requests/{id}/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<RatingResponseDTO> createRating(
            @PathVariable Long id,
            @Valid @RequestBody CreateRatingDTO reqCreateRatingDTO) {
        RatingResponseDTO ratingResponseDTO = ratingService.createRating(id, reqCreateRatingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<RatingResponseDTO>> getRatings() {
        List<RatingResponseDTO> ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }

}