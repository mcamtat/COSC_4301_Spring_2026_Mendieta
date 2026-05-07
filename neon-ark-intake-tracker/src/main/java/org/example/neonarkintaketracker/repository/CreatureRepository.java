package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Creature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository for Creature entities.
 *
 * Inherits CRUD operations from JpaRepository.
 * Creates custom queries for retrieving and validating creature data.
 */
@Repository
public interface CreatureRepository extends JpaRepository<Creature, Long> {


    /**
     * Checks if a creature exists with the same name and habitat.
     *
     * @param name creature name
     * @param habitatId habitat identifier
     * @return true if a duplicate exists, otherwise false
     */
    boolean existsByNameIgnoreCaseAndHabitatId(String name, Long habitatId);


    /**
     * Checks for duplicate creature names within the same habitat,
     * excluding a specific creature ID.
     *
     * Used during rename operation.
     *
     * @param name creature name
     * @param habitatId habitat identifier
     * @param id creature ID to exclude
     * @return true if a duplicate exists, otherwise false
     */
    boolean existsByNameIgnoreCaseAndHabitatIdAndIdNot(String name, Long habitatId, Long id);


    /**
     * Retrieves all active creatures along with their habitat information.
     *
     * Excludes creatures marked as REMOVED and performs JOIN FETCH
     * to include habitat data in a single query.
     *
     * @return list of active creatures with habitat data
     */
    @Query("""
    SELECT c 
    FROM Creature c 
    JOIN FETCH c.habitat 
    WHERE c.status <> 'REMOVED'
    ORDER BY c.id
""")
    List<Creature> findAllActiveWithHabitat();


    /**
     * Retrieves a single creature by ID along with its habitat.
     *
     * @param id the identifier of the creature
     * @return optional containing the creature if found
     */
    @Query("SELECT c FROM Creature c JOIN FETCH c.habitat WHERE c.id = :id")
    Optional<Creature> findByIdWithHabitat(Long id);

}