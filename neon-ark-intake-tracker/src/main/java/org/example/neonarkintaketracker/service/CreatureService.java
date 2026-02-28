package org.example.neonarkintaketracker.service;

import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.entity.Habitat;
import org.example.neonarkintaketracker.repository.CreatureRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.example.neonarkintaketracker.dto.CreatureRequest;
import org.example.neonarkintaketracker.dto.CreatureResponse;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

/*
 * Thin service for now.
 * Keeps the controller clean and gives us a place to add
 * validation, DTO mapping, and business rules later.
 */
@Service
public class CreatureService {

    private final CreatureRepository repository;

    @PersistenceContext
    private EntityManager em;

    public CreatureService(CreatureRepository repository) {

        this.repository = repository;
    }

    /*
     * Return every creature currently in the database.
     * This is the "Read" operation for GET /api/creatures
     */
    public List<Creature> getAllCreatures() {

        return repository.findAll();
    }

    // NEW: Return one creature by id (Optional = may not exist)
    public Optional<Creature> getCreatureById(Long id) {

        return repository.findById(id);
    }

    public CreatureResponse createCreature(CreatureRequest req) {

        // 1. Map DTO -> Entity
        Creature creature = new Creature();
        creature.setName(req.name());
        creature.setSpecies(req.species());
        creature.setDangerLevel(req.dangerLevel());
        creature.setCondition(req.condition());
        creature.setCreatedAt(LocalDateTime.now());
        creature.setNotes(req.notes());

        creature.setHabitat(em.getReference(Habitat.class, req.habitatId()));

        // 2. Save entity
        Creature saved = repository.save(creature);

        // 3. Map Entity -> Response DTO
        return new CreatureResponse(
                saved.getId(),
                saved.getName(),
                saved.getSpecies(),
                saved.getDangerLevel(),
                saved.getCondition(),
                saved.getNotes(),                      // add this
                saved.getHabitat().getId(),           // add this
                saved.getCreatedAt().toString()
        );
    }



}