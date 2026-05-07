package org.example.neonarkintaketracker.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * DTO used for READ responses going back to the Java CLI client.
 *
 * Represents the safe, external view of a creature.
 *
 * NOTE:
 *  - Includes id and createdAt since they are server-generated
 *  - Clients can view these values but cannot modify them
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreatureResponse(
    Long id,                                       // Safe server-generated identifier.
    String name,                                   // Creature name.
    String species,                                // Species label.
    String dangerLevel,                            // Danger level for display.
    String condition,                              // Condition for display.
    String notes,                                  // Notes returned by design choice.
    Long habitatId,                                // Relationship returned as ID only.
    String habitatName,                            // Name associated with ID
    String createdAt                               // Timestamp formatted for display.
) {}