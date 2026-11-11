package com.csis231.springpostgrescrud.dto;


import com.csis231.springpostgrescrud.entity.VoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/**
 * Data Transfer Object for Comment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    /** The comment's unique identifier. */
    private Long id;

    /** The text content of the comment. Required on input. */
    private String content;

    /** The timestamp when the comment was created. */
    private Instant createdAt;

    /**
     * The net vote count on this comment.
     */
    private Integer voteCount;

    /**
     * The ID of the user who wrote this comment.
     */
    private Long authorId;

    /**
     * The ID of the post this comment belongs to.
     */
    private Long postId;

    /**
     * The ID of the parent comment, if this is a reply.
     */
    private Long parentCommentId;

    /**
     * The nested list of direct replies to this comment.
     */
    private List<CommentDto> replies;

    /**
     * The current authenticated client's vote on this comment.
     */
    private VoteType userVote;
}