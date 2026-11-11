package com.csis231.springpostgrescrud.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a vote cast by a client on a post.
 *
 * Maps to the post_votes table. Each row records that a specific client
 * voted on a specific post with either an UPVOTE or DOWNVOTE. A client can only
 * vote once per post — enforced by a unique constraint on (client_id, post_id)
 * at the database level.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "post_id"})
)
public class PostVote {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Whether this is an upvote or downvote.
     * Stored as a string in the database (e.g. "UPVOTE", "DOWNVOTE") via EnumType.STRING.
     * Use VoteType.getType() to get the numeric direction (+1 or -1) for arithmetic.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;

    /**
     * The client who cast this vote. Required.
     * Foreign key: client_id in the post_votes table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * The post this vote was cast on. Required.
     * Foreign key: post_id in the post_votes table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}