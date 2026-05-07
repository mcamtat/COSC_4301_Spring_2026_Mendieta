package org.example.neonarkintaketracker.controller;

import org.example.neonarkintaketracker.service.CreatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/feedings")
public class FeedingController {
    private final CreatureService service;

    public FeedingController(CreatureService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> findFeedings(@RequestParam String time) {

        try {
            var creatures = service.findCreaturesToFeed(time);

            if (creatures.isEmpty()) {
                return ResponseEntity.ok(creatures);
            }

            return ResponseEntity.ok(creatures);

        } catch (RuntimeException e) {

            if ("BAD_REQUEST".equals(e.getMessage())) {
                return ResponseEntity.badRequest()
                        .body("Time must be in HH:MM format");
            }

            return ResponseEntity.internalServerError().build();
        }
    }
}
