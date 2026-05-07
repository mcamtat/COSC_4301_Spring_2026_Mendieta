package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Creature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Simple CRUD + paging/sorting out of the box
@Repository
public interface CreatureRepository extends JpaRepository<Creature, Long> {

    List<Creature> findByStatusNot(String status);

    boolean existsByNameIgnoreCaseAndHabitatId(String name, Long habitatId);

    boolean existsByNameIgnoreCaseAndHabitatIdAndIdNot(String name, Long habitatId, Long id);

    @Query("""
    SELECT c 
    FROM Creature c 
    JOIN FETCH c.habitat 
    WHERE c.status <> 'REMOVED'
    ORDER BY c.id
""")
    List<Creature> findAllActiveWithHabitat();

    @Query("SELECT c FROM Creature c JOIN FETCH c.habitat WHERE c.id = :id")
    Optional<Creature> findByIdWithHabitat(Long id);

    // No extra methods needed for basic "read" functionality

    // Core CRUD methods you get for free:
    // save(entity)        -> insert or update a creature
    // findById(id)        -> get one creature by primary key
    // findAll()           -> get all creatures
    // deleteById(id)      -> delete by primary key
    // delete(entity)      -> delete by passing the entity itself

    // Paging and sorting methods are also included automatically.
}