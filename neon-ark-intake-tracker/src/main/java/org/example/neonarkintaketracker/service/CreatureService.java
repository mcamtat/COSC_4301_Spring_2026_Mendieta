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

/**
 * Service layer responsible for handling business logic and
 * acts as the mediator between repository and controller.
 * <p>
 * Responsibilities:
 *   - Validate incoming data early
 *   - Enforce business rules
 *   - Coordinate repositories
 *   - Map internal Entities to external DTO objects
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


    /**
     * Retrieves every creature marked as active as well as their habitats.
     *
     * Includes habitat data and filter out creatures marked as REMOVED
     *
     * API route: GET /api/creatures
     *
     * @return list of active creatures
     */
    public List<CreatureResponse> getAllCreatures() {

        return repository.findAllActiveWithHabitat()
                .stream()
                .map(c -> new CreatureResponse(
                        c.getId(),
                        c.getName(),
                        c.getSpecies(),
                        c.getDangerLevel(),
                        c.getCondition(),
                        c.getNotes(),
                        c.getHabitat().getId(),
                        c.getHabitat().getLocation(),
                        c.getCreatedAt().toString()
                ))
                .toList();
    }


    /**
     * Retrieves a single creature by its ID.
     *
     * Return a creature only if it exists and is not marked as REMOVED.
     * Otherwise, an empty result is returned.
     *
     * API route: GET /api/creatures/{id}
     *
     * @param id The identifier of the creature
     * @return Optional containing the creature if found and active, otherwise empty
     */
    public Optional<CreatureResponse> getCreatureById(Long id) {

        return repository.findByIdWithHabitat(id)
                .filter(creature -> !"REMOVED".equals(creature.getStatus()))
                .map(creature -> new CreatureResponse(
                        creature.getId(),
                        creature.getName(),
                        creature.getSpecies(),
                        creature.getDangerLevel(),
                        creature.getCondition(),
                        creature.getNotes(),
                        creature.getHabitat().getId(),
                        creature.getHabitat().getLocation(),
                        creature.getCreatedAt().toString()
                ));
    }


    /**
     * Creates a new creature in the system.
     *
     * Rules:
     *  - Name must not be blank
     *  - Danger level must be LOW, MEDIUM, or HIGH
     *  - Condition must be STABLE, QUARANTINED, or CRITICAL
     *  - Habitat must exist
     *  - No duplicate names within the same habitat
     *
     * API route: POST /api/creatures
     *
     * @param req The request containing creature data.
     * @return the created creature as a CreatureResponse
     */
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

        Creature saved = repository.save(creature);

        return new CreatureResponse(
                saved.getId(),
                saved.getName(),
                saved.getSpecies(),
                saved.getDangerLevel(),
                saved.getCondition(),
                saved.getNotes(),
                saved.getHabitat().getId(),
                saved.getHabitat().getLocation(),
                saved.getCreatedAt().toString()
        );
    }


    /**
     * Perform soft-delete on a creature by marking its status as REMOVED.
     *
     * Rules:
     *  - Creature must exist
     *  - Creature must not already be in REMOVED status
     *  - Creatures with active feeding schedules cannot be removed
     *
     * API Route: DELETE /api/creatures/{id}
     *
     * @param id The identifier of the creature
     */
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


    /**
     * Renames an existing creature.
     *
     * Validates the new name and ensures it does not duplicate another creature
     * in the same habitat.
     *
     * API Route: PUT /api/creatures/{id}/name
     *
     * @param id The identifier of the creature
     * @param newName The new name assigned to the creature
     * @return the updated creature after renaming
     */
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


    /**
     * Retrieves all observations for a specific creature.
     *
     * Returns the observations only if the creature exists and is not marked as REMOVED.
     *
     * API Route: GET /api/creatures/{id}/observations
     *
     * @param creatureId The identifier of the creature
     * @return list of observations for the specified creature
     */
    public List<Observation> getObservations(Long creatureId) {

        Creature creature = repository.findById(creatureId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if ("REMOVED".equals(creature.getStatus())) {
            throw new RuntimeException("NOT_FOUND");
        }

        return observationRepository.findByCreatureId(creatureId);
    }


    /**
     * Adds a new observation to a specific creature.
     *
     * Rules:
     *   - Creature must exist
     *   - Status is not REMOVED
     *   - Observation text is not empty
     *
     * Automatically assigns timestamp and author.
     *
     * API Route: POST /api/creatures/{id}/observations
     *
     * @param creatureId the identifier of the creature
     * @param note the observation text
     * @return the saved observation
     */
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
        obs.setAuthor("System");
        obs.setCreature(creature);

        return observationRepository.save(obs);
    }


    /**
     * Retrieves feeding schedule for a specific creature.
     *
     * Returns the feeding schedule only if the creature exists and is not marked as REMOVED.
     *
     * API Route: GET /api/creatures/{id}/feeding-schedule
     *
     * @param creatureId the identifier of the creature
     * @return list of feedings for the specified creature
     */
    public List<FeedingSchedule> getFeedingSchedule(Long creatureId) {

        Creature creature = repository.findById(creatureId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if ("REMOVED".equals(creature.getStatus())) {
            throw new RuntimeException("NOT_FOUND");
        }

        return feedingRepository.findByCreatureId(creatureId);
    }


    /**
     * Adds a new feeding schedule entry for a specific creature.
     *
     * Rules:
     *  - Creature must exist
     *  - Status is not REMOVED
     *  - Feeding time is in valid format
     *
     * API Route: POST /api/creatures/{id}/feeding-schedule
     *
     * @param creatureId the identifier of the creature
     * @param feedTime the scheduled feeding time
     * @param notes optional notes associated with the feeding
     * @return the saved feeding schedule entry
     */
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


    /**
     * Retrieves all creatures that need to be fed at a specific time.
     *
     * Rules:
     *  - Time format is valid
     *  - Exclude creatures marked as REMOVED
     *
     * API Route: GET /api/feedings?time={HH:MM}
     *
     * @param time the feeding time in HH:MM format
     * @return list of creatures that require feeding at the specified time (may be empty)
     */
    public List<CreatureResponse> findCreaturesToFeed(String time) {

        if (time == null || !time.matches("^\\d{2}:\\d{2}$")) {
            throw new RuntimeException("BAD_REQUEST");
        }

        return feedingRepository.findCreaturesByFeedTime(time)
                .stream()
                .filter(c -> !"REMOVED".equals(c.getStatus()))
                .map(c -> new CreatureResponse(
                        c.getId(),
                        c.getName(),
                        c.getSpecies(),
                        c.getDangerLevel(),
                        c.getCondition(),
                        c.getNotes(),
                        c.getHabitat().getId(),
                        c.getHabitat().getLocation(),
                        c.getCreatedAt().toString()
                ))
                .toList();
    }

}