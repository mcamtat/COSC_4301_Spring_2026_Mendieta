package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository for User entities.
 * Inherits CRUD operations from JpaRepository.
 *
 * Used by admin service to retrieve user and role data.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}