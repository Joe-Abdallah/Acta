package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for CommentVote entities.
 * Extends JpaRepository which provides standard CRUD operations
 */
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    Optional<CommentVote> findByCommentIdAndClientId(Long commentId, Long clientId);
}
