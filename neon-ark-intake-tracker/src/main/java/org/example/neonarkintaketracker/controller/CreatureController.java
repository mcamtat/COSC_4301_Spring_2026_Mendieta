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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * This controller handles incoming HTTP requests for /api/creatures
 */
@RestController
@RequestMapping("/api/creatures")
public class CreatureController {

    private final CreatureService service;

    // Constructor-based Dependency Injection (DI)
    public CreatureController(CreatureService service) {
        this.service = service;
    }

    /*
     * Map HTTP GET requests at /api/creatures to this method
     * Example: GET http://localhost:8080/api/creatures
     */

    @GetMapping
    public ResponseEntity<List<Creature>> getAllCreatures() {

        List<Creature> creatures = service.getAllCreatures();
        // Return 200 OK with JSON body
        return ResponseEntity.ok(creatures);
    }


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


    // NEW: GET /api/creatures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Creature> getCreatureById(@PathVariable Long id) {

        Optional<Creature> maybeCreature = service.getCreatureById(id);

        if (maybeCreature.isEmpty()) {
            // 404 when id does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 200 OK when found
        return ResponseEntity.ok(maybeCreature.get());
    }


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

    @GetMapping("/{id}/observations")
    public ResponseEntity<?> getObservations(@PathVariable Long id) {

        try {
            List<Observation> observations = service.getObservations(id);
            return ResponseEntity.ok(observations);

        } catch (RuntimeException e) {

            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Creature not found");
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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
