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

import com.bonkers.maintenance_system.dto.CommentResponseDTO;
import com.bonkers.maintenance_system.dto.CreateCommentDTO;
import com.bonkers.maintenance_system.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maintenance-requests/{id}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentDTO reqCommentDTO) {
        CommentResponseDTO commentResponseDTO = commentService.createComment(id, reqCommentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long id) {
        List<CommentResponseDTO> comments = commentService.getComment(id);
        return ResponseEntity.ok(comments);
    }
}