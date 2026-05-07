package org.example.neonarkintaketracker.entity;

import jakarta.persistence.*;
import lombok.*;


/**
 * Entity representing a user role in the system.
 *
 * Database: roles
 *
 * Notes:
 * - Roles are assigned to users to control access to endpoints
 * - Defines permission levels
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}