package org.example.neonarkintaketracker.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "habitats")
public class Habitat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String biome;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(name = "min_temp_c", nullable = false)
    private Integer minTempC;

    @Column(name = "max_temp_c", nullable = false)
    private Integer maxTempC;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // One Habitat -> Many Creatures
    @OneToMany(mappedBy = "habitat")
    @JsonManagedReference
    private List<Creature> creatures;
}