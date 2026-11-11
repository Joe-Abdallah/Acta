package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for PostVote entities.
 * Extends JpaRepository which provides standard CRUD operations
 */
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
    Optional<PostVote> findByPostIdAndClientId(Long postId, Long clientId);
}
