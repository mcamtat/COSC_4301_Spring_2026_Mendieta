package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.entity.FeedingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Repository for FeedingSchedule entities.
 *
 * Inherits CRUD operations from JpaRepository.
 * Provides queries for retrieving feeding schedules and
 * identifying creatures based on feeding times.
 */
public interface FeedingScheduleRepository extends JpaRepository<FeedingSchedule, Long> {

    List<FeedingSchedule> findByCreatureId(Long creatureId);


    /**
     * Retrieves distinct creatures that need to be fed at a given time.
     * Includes habitat information using JOIN FETCH.
     */
    @Query("""
    SELECT DISTINCT c
    FROM FeedingSchedule f
    JOIN f.creature c
    JOIN FETCH c.habitat
    WHERE FUNCTION('TO_CHAR', f.feedTime, 'HH24:MI') = :time
""")
    List<Creature> findCreaturesByFeedTime(@Param("time") String time);

    boolean existsByCreatureIdAndFeedTimeAfter(Long creatureId, LocalDateTime now);

}
