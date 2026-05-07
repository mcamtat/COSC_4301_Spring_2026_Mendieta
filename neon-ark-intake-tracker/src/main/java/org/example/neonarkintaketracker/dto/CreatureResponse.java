package org.example.neonarkintaketracker.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * DTO used for READ responses going back to the Java CLI client.
 *
 * NOTE:
 *  - We INCLUDE id and createdAt because the server/database controls them.
 *  - This is the "allowed" shape of outgoing data.
 *  - The client can see these values, but should never be able to set them.
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