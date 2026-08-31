package com.bonkers.maintenance_system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonkers.maintenance_system.dto.LoginDTO;
import com.bonkers.maintenance_system.dto.RegisterDTO;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.service.AuthService;
import com.bonkers.maintenance_system.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    private final JwtService jwtService;

    // Constructor to initialize AuthService and JwtService
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterDTO reqDto) {
        return ResponseEntity.ok(authService.register(reqDto));
    }

    // Authenticate user and generate JWT token
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDTO reqDto) {
        User user = authService.login(reqDto);
        String token = jwtService.generateToken(user.getEmail(), user.getRole().toString());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

}