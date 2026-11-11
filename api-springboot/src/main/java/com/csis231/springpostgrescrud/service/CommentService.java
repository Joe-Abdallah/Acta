package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.CommentDto;
import java.util.List;

/**
 * Service interface for managing {@link com.csis231.springpostgrescrud.entity.Comment} entities.
 * Supports nested replies through parentCommentId.
 */
public interface CommentService {

    /**
     * Creates a new comment. If parentCommentId is provided, the comment becomes a reply.
     *
     * @param commentDto DTO containing content, authorId, postId, and optionally parentCommentId
     * @return the created comment DTO (without nested replies)
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if author, post, or parent comment not found
     */
    CommentDto createComment(CommentDto commentDto);

    /**
     * Retrieves a comment by its ID.
     *
     * @param id the comment ID
     * @return the comment DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if not found
     */
    CommentDto getCommentById(Long id);

    /**
     * Retrieves all top‑level comments for a specific post.
     * Replies are nested inside the parent comment DTOs (if needed).
     *
     * @param postId the post ID
     * @return list of top‑level comment DTOs
     */
    List<CommentDto> getCommentsByPost(Long postId);

    /**
     * Updates a comment's content.
     *
     * @param id          the comment ID
     * @param newContent  the new text content
     * @return the updated comment DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if comment not found
     */
    CommentDto updateComment(Long id, String newContent);

    /**
     * Deletes a comment by ID. All replies are cascade‑deleted automatically.
     *
     * @param id the comment ID
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if comment not found
     */
    void deleteComment(Long id);
}