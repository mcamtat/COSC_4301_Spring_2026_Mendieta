package org.example.neonarkintaketracker.service;

import org.example.neonarkintaketracker.dto.UserResponse;
import org.example.neonarkintaketracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll().stream()
                .map(u -> new UserResponse(
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getRole() != null ? u.getRole().getName() : ""
                ))
                .toList();
    }
}