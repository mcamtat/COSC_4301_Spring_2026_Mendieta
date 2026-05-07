package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Repository for Observation entities.
 *
 * Inherits CRUD operations from JpaRepository.
 */
public interface ObservationRepository extends JpaRepository<Observation, Long> {


    /**
     * Retrieves all recorded observations of a specific creature.
     *
     * @param creatureId the identifier of the creature
     * @return list of observations for the creature
     */
    List<Observation> findByCreatureId(Long creatureId);
}