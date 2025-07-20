package org.unitech.msauth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unitech.msauth.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByFin(String fin);

    boolean existsByFin(String fin);

    boolean existsByEmail(String email);
}