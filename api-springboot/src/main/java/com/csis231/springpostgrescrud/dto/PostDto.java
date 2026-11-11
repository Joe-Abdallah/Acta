package com.csis231.springpostgrescrud.dto;

import com.csis231.springpostgrescrud.entity.VoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Data Transfer Object for Post..
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    /** The post's unique identifier. */
    private Long id;

    /** The post title. Required on creation. */
    private String title;

    /** The text body of the post. */
    private String content;

    /** The timestamp when the post was created. */
    private Instant createdAt;

    /**
     * The net vote count (upvotes minus downvotes).
     */
    private Integer voteCount;

    /**
     * The total number of comments on this post.
     */
    private Integer commentCount;

    /**
     * The ID of the client who authored this post.
     */
    private Long authorId;

    /**
     * The ID of the community this post belongs to.
     */
    private Long communityId;

    /**
     * The current authenticated client's vote on this post.
     * UPVOTE or DOWNVOTE if the user has voted, null otherwise.
     */
    private VoteType userVote;
}