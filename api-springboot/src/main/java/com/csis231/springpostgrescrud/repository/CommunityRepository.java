package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Community entities.
 * Extends JpaRepository which provides standard CRUD operations
 */
public interface CommunityRepository extends JpaRepository<Community, Long> {
    boolean existsByNameIgnoreCase(String trim);
}
