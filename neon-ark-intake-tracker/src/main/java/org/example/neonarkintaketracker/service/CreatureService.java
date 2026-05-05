package org.example.neonarkintaketracker.service;

import org.example.neonarkintaketracker.dto.UserResponse;
import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.entity.FeedingSchedule;
import org.example.neonarkintaketracker.entity.Habitat;
import org.example.neonarkintaketracker.entity.Observation;
import org.example.neonarkintaketracker.repository.CreatureRepository;
import org.example.neonarkintaketracker.repository.FeedingScheduleRepository;
import org.example.neonarkintaketracker.repository.ObservationRepository;
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
    private final ObservationRepository observationRepository;
    private final FeedingScheduleRepository feedingRepository;

    @PersistenceContext
    private EntityManager em;

    public CreatureService(CreatureRepository repository, ObservationRepository observationRepository, FeedingScheduleRepository feedingRepository) {

        this.repository = repository;
        this.observationRepository = observationRepository;
        this.feedingRepository = feedingRepository;
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

        creature.setStatus("ACTIVE");

        if (!List.of("LOW","MEDIUM","HIGH").contains(req.dangerLevel())) {
            throw new RuntimeException("BAD_REQUEST");
        }

        if (!List.of("STABLE","QUARANTINED","CRITICAL").contains(req.condition())) {
            throw new RuntimeException("BAD_REQUEST");
        }

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

        if ("REMOVED".equals(creature.getStatus())) {
            throw new RuntimeException("NOT_FOUND");
        }

        if (feedingRepository.existsByCreatureIdAndFeedTimeAfter(id, LocalDateTime.now())) {
            throw new RuntimeException("ACTIVE_FEEDING_SCHEDULE");
        }

        creature.setStatus("REMOVED");
        repository.save(creature);
    }

    public Creature renameCreature(Long id, String newName) {

        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException("BAD_REQUEST");
        }

        Creature creature = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (repository.existsByNameIgnoreCaseAndHabitatIdAndIdNot(
                newName,
                creature.getHabitat().getId(),
                creature.getId()
        )) {
            throw new RuntimeException("DUPLICATE_NAME");
        }

            creature.setName(newName);
            return repository.save(creature);
        }


    public List<Observation> getObservations(Long creatureId) {

        Creature creature = repository.findById(creatureId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if ("REMOVED".equals(creature.getStatus())) {
            throw new RuntimeException("NOT_FOUND");
        }

        return observationRepository.findByCreatureId(creatureId);
    }

    public Observation addObservation(Long creatureId, String note) {

        Creature creature = repository.findById(creatureId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if ("REMOVED".equals(creature.getStatus())) {
            throw new RuntimeException("CONFLICT_REMOVED");
        }

        if (note == null || note.trim().isEmpty()) {
            throw new RuntimeException("BAD_REQUEST");
        }

        Observation obs = new Observation();
        obs.setNote(note);
        obs.setObservedAt(LocalDateTime.now());
        obs.setCreature(creature);

        return observationRepository.save(obs);
    }

    public List<FeedingSchedule> getFeedingSchedule(Long creatureId) {

        Creature creature = repository.findById(creatureId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if ("REMOVED".equals(creature.getStatus())) {
            throw new RuntimeException("NOT_FOUND");
        }

        return feedingRepository.findByCreatureId(creatureId);
    }

    public FeedingSchedule addFeeding(Long creatureId, LocalDateTime feedTime, String notes) {

        Creature creature = repository.findById(creatureId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if ("REMOVED".equals(creature.getStatus())) {
            throw new RuntimeException("CONFLICT_REMOVED");
        }

        if (feedTime == null) {
            throw new RuntimeException("BAD_REQUEST");
        }

        FeedingSchedule fs = new FeedingSchedule();
        fs.setFeedTime(feedTime);
        fs.setNotes(notes);
        fs.setCreature(creature);

        return feedingRepository.save(fs);
    }

    public List<Creature> findCreaturesToFeed(String time) {

        if (time == null || !time.matches("^\\d{2}:\\d{2}$")) {
            throw new RuntimeException("BAD_REQUEST");
        }

        List<FeedingSchedule> schedules = feedingRepository.findByFeedTimeString(time);

        return schedules.stream()
                .map(FeedingSchedule::getCreature)
                .filter(c -> !"REMOVED".equals(c.getStatus()))
                .toList();
    }

}