package org.example.neonarkintaketracker.controller;

import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.service.CreatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.example.neonarkintaketracker.dto.CreatureRequest;
import org.example.neonarkintaketracker.dto.CreatureResponse;
import jakarta.validation.Valid;


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

}
