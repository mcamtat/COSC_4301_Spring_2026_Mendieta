package org.example.neonarkintaketracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "feeding_schedules")
public class FeedingSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feed_time", nullable = false)
    private LocalDateTime feedTime;

    @Column
    private String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creature_id", nullable = false)
    @JsonIgnore
    private Creature creature;
}
