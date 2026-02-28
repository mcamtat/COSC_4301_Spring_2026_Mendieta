package org.example.neonarkintaketracker.dto;

import lombok.Data;
/**
 * DTO used for CREATE and UPDATE requests coming from the Java CLI client.
 *
 * NOTE:
 *  - We intentionally EXCLUDE id and createdAt because those are DB-managed.
 *  - This is the "allowed" shape of incoming data.
 */
public record CreatureRequest(
        String name,                                   // Allowed input: creature name.
        String species,                                // Allowed input: species label.
        String dangerLevel,                            // Allowed input: danger level.
        String condition,                              // Allowed input: creature condition.
        String notes,                                  // Optional notes from user.
        Long habitatId                                 // Client selects habitat by ID only.
) {}                                                   // No id or createdAt included.