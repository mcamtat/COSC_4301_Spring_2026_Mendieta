package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Creature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Simple CRUD + paging/sorting out of the box
@Repository
public interface CreatureRepository extends JpaRepository<Creature, Long> {

    List<Creature> findByStatusNot(String status);

    // No extra methods needed for basic "read" functionality

    // Core CRUD methods you get for free:
    // save(entity)        -> insert or update a creature
    // findById(id)        -> get one creature by primary key
    // findAll()           -> get all creatures
    // deleteById(id)      -> delete by primary key
    // delete(entity)      -> delete by passing the entity itself

    // Paging and sorting methods are also included automatically.
}