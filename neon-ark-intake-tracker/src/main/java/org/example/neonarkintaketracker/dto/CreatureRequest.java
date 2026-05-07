package org.example.neonarkintaketracker.dto;


/**
 * DTO used for CREATE and UPDATE requests coming from the Java CLI client.
 *
 * Defines the allowed structure of incoming data
 *
 * NOTE:
 *  - Excludes id and createdAt because they are managed by the database
 *  - Used only for input validation and request handling
 */
public record CreatureRequest(
        String name,                                   // Allowed input: creature name.
        String species,                                // Allowed input: species label.
        String dangerLevel,                            // Allowed input: danger level.
        String condition,                              // Allowed input: creature condition.
        String notes,                                  // Optional notes from user.
        Long habitatId                                 // Client selects habitat by ID only.
) {}                                                   // No id or createdAt included.