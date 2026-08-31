package com.bonkers.maintenance_system.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bonkers.maintenance_system.dto.LoginDTO;
import com.bonkers.maintenance_system.dto.RegisterDTO;
import com.bonkers.maintenance_system.model.User;
import com.bonkers.maintenance_system.repository.UserRepository;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterDTO reqDto) {
        if(userRepository.findByEmail(reqDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setName(reqDto.getName());
        user.setEmail(reqDto.getEmail());
        user.setPassword(passwordEncoder.encode(reqDto.getPassword()));
        user.setRole(reqDto.getRole());
        return userRepository.save(user);
    }

    public User login(LoginDTO reqLoginDTO) {
        User user = userRepository.findByEmail(reqLoginDTO.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if(!passwordEncoder.matches(reqLoginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }
}