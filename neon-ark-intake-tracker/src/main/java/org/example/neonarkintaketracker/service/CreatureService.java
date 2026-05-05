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

        return repository.findByStatusNot("REMOVED");
    }

    // NEW: Return one creature by id (Optional = may not exist)
    public Optional<Creature> getCreatureById(Long id) {

        return repository.findById(id);
    }

    public CreatureResponse createCreature(CreatureRequest req) {

        if (req.name() == null || req.name().trim().isEmpty()) {
            throw new RuntimeException("BAD_REQUEST");
        }

        if (repository.existsByNameIgnoreCaseAndHabitatId(req.name(), req.habitatId())) {
            throw new RuntimeException("DUPLICATE_CREATURE");
        }

        Habitat habitat = em.find(Habitat.class, req.habitatId());
        if (habitat == null) {
            throw new RuntimeException("BAD_REQUEST");
        }
        // 1. Map DTO -> Entity
        Creature creature = new Creature();
        creature.setName(req.name());
        creature.setSpecies(req.species());
        creature.setDangerLevel(req.dangerLevel());
        creature.setCondition(req.condition());
        creature.setCreatedAt(LocalDateTime.now());
        creature.setNotes(req.notes());

        creature.setHabitat(habitat);

        // 2. Save entity
        Creature saved = repository.save(creature);

        // 3. Map Entity -> Response DTO
        return new CreatureResponse(
                saved.getId(),
                saved.getName(),
                saved.getSpecies(),
                saved.getDangerLevel(),
                saved.getCondition(),
                saved.getNotes(),
                saved.getHabitat().getId(),
                saved.getCreatedAt().toString()
        );
    }


    public void deleteCreature(Long id) {
        Creature creature = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        creature.setStatus("REMOVED");
        repository.save(creature);
    }

    public Creature renameCreature(Long id, String newName) {

        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException("BAD_REQUEST");
        }

        Creature creature = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (repository.existsByNameIgnoreCaseAndHabitatId(newName, creature.getHabitat().getId()) && !creature.getName().equalsIgnoreCase(newName)){
            throw new RuntimeException("DUPLICATE_NAME");
        }

            creature.setName(newName);
            return repository.save(creature);
        }

}