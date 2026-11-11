package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Comment entities.
 * Extends JpaRepository which provides standard CRUD operations
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_IdAndParentCommentIsNull(Long postId);

    long countByPostId(Long postId);
}
