package org.example.neonarkintaketracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * Entity representing an observations recorded for a creature.
 *
 * Database: observations
 *
 * Notes:
 * - Each observation is associated with a single creature
 * - Multiple observations can exist for one creature
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "observations")
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String note;

    @Column(name = "observed_at", nullable = false)
    private LocalDateTime observedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creature_id", nullable = false)
    @JsonIgnore
    private Creature creature;

    @Column
    private String author;
}
