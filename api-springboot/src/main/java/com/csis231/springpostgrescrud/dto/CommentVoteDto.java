package com.csis231.springpostgrescrud.dto;

import com.csis231.springpostgrescrud.entity.VoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for CommentVote
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentVoteDto {

    /** The vote's unique identifier. */
    private Long id;

    /** Whether this vote is an upvote or downvote. */
    private VoteType voteType;

    /** The ID of the client who cast this vote. */
    private Long userId;

    /** The ID of the comment this vote was cast on. */
    private Long commentId;
}