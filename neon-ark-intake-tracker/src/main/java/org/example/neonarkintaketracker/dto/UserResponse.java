package org.example.neonarkintaketracker.dto;


/**
 * DTO used for returning user information to the CLI client.
 *
 * Exposes basic contact details and role information solely to administrators.
 */
public record UserResponse(
        String fullName,
        String email,
        String phone,
        String role
) {}