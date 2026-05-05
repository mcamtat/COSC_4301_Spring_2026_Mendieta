package org.example.neonarkintaketracker.repository;

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

    boolean existsByCreatureIdAndFeedTimeAfter(Long creatureId, LocalDateTime now);

}
