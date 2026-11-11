package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.CommentVoteDto;
import com.csis231.springpostgrescrud.entity.VoteType;

/**
 * Service interface for managing votes on comments.
 * Each client can vote only once per comment.
 */
public interface CommentVoteService {

    /**
     * Casts a vote on a comment. If the user already voted, the vote is updated.
     *
     * @param commentId the ID of the comment
     * @param clientId  the ID of the client
     * @param voteType  UPVOTE or DOWNVOTE
     * @return the saved vote DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if comment or client not found
     */
    CommentVoteDto castVote(Long commentId, Long clientId, VoteType voteType);

    /**
     * Retrieves a client's vote on a specific comment.
     *
     * @param commentId the comment ID
     * @param clientId  the client ID
     * @return the vote DTO, or {@code null} if no vote exists
     */
    CommentVoteDto getVote(Long commentId, Long clientId);

    /**
     * Removes a client's vote from a comment.
     *
     * @param commentId the comment ID
     * @param clientId  the client ID
     */
    void deleteVote(Long commentId, Long clientId);
}