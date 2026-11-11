package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.PostDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Community;
import com.csis231.springpostgrescrud.entity.Post;
import com.csis231.springpostgrescrud.entity.VoteType;

/**
 * Utility class for converting between Post entities and PostDto objects.
 *
 * All methods are static — this class is never instantiated.
 * It has no dependencies and performs pure data mapping only.
 */
public class PostMapper {

    /**
     * Converts a Post entity to a {@link PostDto} including the current client's vote state.
     * @param post      the entity to convert; may be {@code null}
     * @param userVote  the current user's vote on this post ({@code UPVOTE}, {@code DOWNVOTE}
     * @return a fully populated {@link PostDto}, or {@code null} if {@code post} is {@code null}
     */
    public static PostDto toDto(Post post, VoteType userVote) {
        if (post == null) return null;
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getVoteCount(),
                post.getCommentCount(),
                post.getAuthor().getId(),
                post.getCommunity().getId(),
                userVote
        );
    }

    /**
     * Converts a {@link Post} entity to a {@link PostDto} with no vote state.
     * @param post the entity to convert; may be {@code null}
     * @return a {@link PostDto} with {@code userVote = null},
     *         or {@code null} if {@code post} is {@code null}
     */
    public static PostDto toDto(Post post) {
        return toDto(post, null);
    }

    /**
     * Converts a {@link PostDto} to a new {@link Post} entity.
     * @param dto       the DTO containing client-provided data; may be {@code null}
     * @param author    the resolved {@link Client} entity for the post's author
     * @param community the resolved {@link Community} entity the post is submitted to
     * @return a new {@link Post} entity ready to be saved, or {@code null} if {@code dto} is {@code null}
     */
    public static Post toEntity(PostDto dto, Client author, Community community) {
        if (dto == null) return null;
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setVoteCount(0);
        post.setCommentCount(0);
        post.setAuthor(author);
        post.setCommunity(community);
        return post;
    }
}