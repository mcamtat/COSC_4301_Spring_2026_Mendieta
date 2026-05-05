package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservationRepository extends JpaRepository<Observation, Long> {

    List<Observation> findByCreatureId(Long creatureId);
}