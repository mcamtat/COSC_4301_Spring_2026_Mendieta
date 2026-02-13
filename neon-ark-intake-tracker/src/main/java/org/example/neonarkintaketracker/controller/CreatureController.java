package org.example.neonarkintaketracker.controller;

import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.service.CreatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.example.neonarkintaketracker.dto.CreatureRequest;
import org.example.neonarkintaketracker.dto.CreatureResponse;
import jakarta.validation.Valid;


import java.util.List;
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

    // NEW: GET /api/creatures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Creature> getCreatureById(@PathVariable Long id) {

        Optional<Creature> maybeCreature = service.getCreatureById(id);

        if (maybeCreature.isEmpty()) {
            // 404 when id does not exist
            return ResponseEntity.notFound().build();
        }

        // 200 OK when found
        return ResponseEntity.ok(maybeCreature.get());
    }

    @PostMapping
    public ResponseEntity<CreatureResponse> create(@Valid @RequestBody CreatureRequest req) {

        CreatureResponse created = service.createCreature(req);

        return ResponseEntity.status(201).body(created);
    }



}
