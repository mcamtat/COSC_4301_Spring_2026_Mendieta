package org.example.neonarkintaketracker.service;

import org.example.neonarkintaketracker.dto.UserResponse;
import org.example.neonarkintaketracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service layer responsible for administrative and user management operations.
 *
 * Acts as the mediator between repository and controller for user-related data.
 *
 * Responsibilities:
 *   - Retrieve system user data
 *   - Map internal User entities to external UserResponse DTO objects
 */
@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Retrieves all registered users in the system.
     *
     * Maps User entities to UserResponse DTOs to expose only
     * the required fields (full name, email, phone, role).
     *
     * API Route: GET /api/admin/users
     *
     * @return list of all users with role information
     */
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