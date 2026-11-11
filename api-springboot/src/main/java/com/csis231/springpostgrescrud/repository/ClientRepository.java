package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Client entities.
 * Extends JpaRepository which provides standard CRUD operations
 */

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUsernameIgnoreCase(String username);

    Optional<Client> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);
}
