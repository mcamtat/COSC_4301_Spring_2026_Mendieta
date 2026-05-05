package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}