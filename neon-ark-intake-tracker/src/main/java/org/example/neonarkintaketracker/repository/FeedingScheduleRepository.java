package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.entity.FeedingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedingScheduleRepository extends JpaRepository<FeedingSchedule, Long> {

    @Query("""
    SELECT f FROM FeedingSchedule f
    WHERE FUNCTION('TO_CHAR', f.feedTime, 'HH24:MI') = :time
""")
    List<FeedingSchedule> findByFeedTimeString(@Param("time") String time);
    List<FeedingSchedule> findByCreatureId(Long creatureId);

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
