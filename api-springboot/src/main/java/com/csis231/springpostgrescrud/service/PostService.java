package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.PostDto;
import java.util.List;

/**
 * Service interface for managing {@link com.csis231.springpostgrescrud.entity.Post} entities.
 * Provides CRUD operations.
 */
public interface PostService {

    /**
     * Summarizes a post's content
     *
     * @param postId Long containing the id of the post
     * @return the summary
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if post not found
     */
    String summarizePostContent(Long postId);

    /**
     * Creates a new post. The DTO must contain authorId and communityId.
     *
     * @param postDto DTO containing title, content, authorId, communityId
     * @return the created post DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if author or community not found
     */
    PostDto createPost(PostDto postDto);

    /**
     * Retrieves a post by its ID.
     *
     * @param id the post ID
     * @return the post DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if not found
     */
    PostDto getPostById(Long id);

    /**
     * Retrieves all posts.
     *
     * @return a list of post DTOs
     */
    List<PostDto> getAllPosts();

    /**
     * Updates an existing post. Only title and content can be updated.
     *
     * @param id       the post ID
     * @param postDto  DTO containing updated title and/or content
     * @return the updated post DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if post not found
     */
    PostDto updatePost(Long id, PostDto postDto);

    /**
     * Deletes a post by ID. All associated comments and votes are cascade‑deleted automatically.
     *
     * @param id the post ID
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if post not found
     */
    void deletePost(Long id);
}