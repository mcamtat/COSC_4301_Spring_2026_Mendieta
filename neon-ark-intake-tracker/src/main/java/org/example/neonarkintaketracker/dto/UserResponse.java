package org.example.neonarkintaketracker.dto;

public record UserResponse(
        String fullName,
        String email,
        String phone,
        String role
) {}