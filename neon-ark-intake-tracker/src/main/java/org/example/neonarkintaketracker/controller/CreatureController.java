package org.example.neonarkintaketracker.controller;

import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.entity.FeedingSchedule;
import org.example.neonarkintaketracker.entity.Observation;
import org.example.neonarkintaketracker.service.CreatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.example.neonarkintaketracker.dto.CreatureRequest;
import org.example.neonarkintaketracker.dto.CreatureResponse;
import jakarta.validation.Valid;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller responsible for creature-related API endpoints.
 *
 * Handles CRUD operations, observations, and feeding schedules.
 */
@RestController
@RequestMapping("/api/creatures")
public class CreatureController {

    private final CreatureService service;

    // Constructor-based Dependency Injection (DI)
    public CreatureController(CreatureService service) {
        this.service = service;
    }


    /**
     * Retrieves all active creatures in the system.
     *
     * API Route: GET /api/creatures
     *
     * @return list of active creatures
     */
    @GetMapping
    public ResponseEntity<List<CreatureResponse>> getAllCreatures() {

        return ResponseEntity.ok(service.getAllCreatures());
    }


    /**
     * Renames an existing creature.
     *
     * Validates input and ensures no duplicate names exist within the same habitat.
     *
     * API Route: PUT /api/creatures/{id}/name
     *
     * @param id the creature ID
     * @param request the new name
     * @return updated creature or error response
     */
    @PutMapping("/{id}/name")
    public ResponseEntity<?> renameCreature(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String newName = request.get("name");

        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name cannot be blank");
        }

        try {
            Creature updated = service.renameCreature(id, newName);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            String msg = e.getMessage();

            return switch (msg == null ? "" : msg) {
                case "DUPLICATE_NAME" -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A creature with that name already exists");
                case "NOT_FOUND" -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Creature not found");
                case "BAD_REQUEST" -> ResponseEntity.badRequest()
                        .body("Invalid name");
                default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
            };

        }
    }


    /**
     * Retrieves a single creature by its ID.
     *
     * API Route: GET /api/creatures/{id}
     *
     * @param id the creature ID
     * @return creature if found, otherwise error response
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreatureResponse> getCreatureById(@PathVariable Long id) {

        Optional<CreatureResponse> maybeCreature = service.getCreatureById(id);

        if (maybeCreature.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(maybeCreature.get());
    }


    /**
     * Creates a new creature.
     *
     * Validates input and enforces business rules.
     *
     * API Route: POST /api/creatures
     *
     * @param req user input containing creature data
     * @return created creature or error response
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreatureRequest req) {
        try {
            CreatureResponse created = service.createCreature(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201

        } catch (RuntimeException e) {
            if ("DUPLICATE_CREATURE".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No duplicates allowed");
            }

            if ("BAD_REQUEST".equals(e.getMessage())) {
                return ResponseEntity.badRequest()
                        .body("Invalid input");
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Invalid input or violates database constraints");
        }
    }


    /**
     * Removes a creature by performing a soft delete.
     *
     * Sets the creature status to REMOVED if found and does not have a feeding schedule..
     *
     * API Route: DELETE /api/creatures/{id}
     *
     * @param id the creature ID
     * @return success or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCreature(@PathVariable Long id) {

        try {
            service.deleteCreature(id);

            return ResponseEntity.ok(
                    Map.of("message", "Status changed to REMOVED")
            );

        } catch (RuntimeException e) {

            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Creature not found");
            }

            if ("ACTIVE_FEEDING_SCHEDULE".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Cannot remove creature with active feeding schedule");
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Retrieves all observations for a specific creature.
     *
     * API Route: GET /api/creatures/{id}/observations
     *
     * @param id the creature ID
     * @return creature details with new observations
     */
    @GetMapping("/{id}/observations")
    public ResponseEntity<?> getObservations(@PathVariable Long id) {
        try {
            CreatureResponse creature = service.getCreatureById(id)
                    .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

            List<Map<String, Object>> obsList = service.getObservations(id)
                    .stream()
                    .map(o -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", o.getId());
                        m.put("note", o.getNote());
                        m.put("author", o.getAuthor());
                        m.put("observedAt", o.getObservedAt().toString());
                        return m;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "id", creature.id(),
                    "name", creature.name(),
                    "habitatName", creature.habitatName(),
                    "observations", obsList
            ));

        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Creature not found");
            }
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * Adds a new observation to a creature.
     *
     * API Route: POST /api/creatures/{id}/observations
     *
     * @param id the creature ID
     * @param body user input containing observation note
     * @return created observation or error response
     */
    @PostMapping("/{id}/observations")
    public ResponseEntity<?> addObservation(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        try {
            Observation saved = service.addObservation(id, body.get("note"));
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (RuntimeException e) {

            return switch (e.getMessage()) {
                case "NOT_FOUND" -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Creature not found");

                case "CONFLICT_REMOVED" -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Cannot add observation to removed creature");

                case "BAD_REQUEST" -> ResponseEntity.badRequest()
                        .body("Invalid note");

                default -> ResponseEntity.internalServerError().build();
            };
        }
    }


    /**
     * Retrieves the feeding schedule for a specific creature.
     *
     * API Route: GET /api/creatures/{id}/feeding-schedule
     *
     * @param id the creature ID
     * @return list of feeding schedules
     */
    @GetMapping("/{id}/feeding-schedule")
    public ResponseEntity<?> getFeedingSchedule(@PathVariable Long id) {

        try {
            return ResponseEntity.ok(service.getFeedingSchedule(id));

        } catch (RuntimeException e) {

            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Creature not found");
            }

            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * Adds a feeding schedule entry for a creature.
     *
     * API Route: POST /api/creatures/{id}/feeding-schedule
     *
     * @param id the creature ID
     * @param body user input containing feed time and notes
     * @return created feeding schedule or error response
     */
    @PostMapping("/{id}/feeding-schedule")
    public ResponseEntity<?> addFeeding(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        LocalDateTime time;

        try {
            time = LocalDateTime.parse(body.get("feedTime"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Invalid feed time format (YYYY-MM-DDTHH:MM:SS)");
        }

        try {
            FeedingSchedule saved = service.addFeeding(
                    id,
                    time,
                    body.get("notes")
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (RuntimeException e) {

            return switch (e.getMessage()) {
                case "NOT_FOUND" -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Creature not found");

                case "CONFLICT_REMOVED" -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Cannot add feeding to removed creature");

                case "BAD_REQUEST" -> ResponseEntity.badRequest()
                        .body("Invalid input");

                default -> ResponseEntity.internalServerError().build();
            };
        }
    }
}
