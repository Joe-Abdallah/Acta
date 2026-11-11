package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.PostVoteDto;
import com.csis231.springpostgrescrud.entity.VoteType;

/**
 * Service interface for managing votes on posts.
 * Each client can vote only once per post.
 */
public interface PostVoteService {

    /**
     * Casts a vote on a post. If the user already voted, the existing vote is updated
     * to the new type (effectively changing the vote).
     *
     * @param postId   the ID of the post being voted on
     * @param clientId the ID of the client casting the vote
     * @param voteType UPVOTE or DOWNVOTE
     * @return the saved vote DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if post or client not found
     */
    PostVoteDto castVote(Long postId, Long clientId, VoteType voteType);

    /**
     * Retrieves a client's vote on a specific post.
     *
     * @param postId   the post ID
     * @param clientId the client ID
     * @return the vote DTO, or {@code null} if no vote exists
     */
    PostVoteDto getVote(Long postId, Long clientId);

    /**
     * Removes a client's vote from a post.
     *
     * @param postId   the post ID
     * @param clientId the client ID
     */
    void deleteVote(Long postId, Long clientId);
}