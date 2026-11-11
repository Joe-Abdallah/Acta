package com.csis231.springpostgrescrud.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a vote cast by a user on a comment.
 *
 * Maps to the comment_votes table. Each row records that a specific client
 * voted on a specific comment with either an UPVOTE or DOWNVOTE. A client can only
 * vote once per comment — enforced by a unique constraint on (client_id, comment_id)
 * at the database level.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "comment_id"})
)
public class CommentVote {

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
     * Foreign key: client_id in the comment_votes table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * The post this vote was cast on. Required.
     * Foreign key: comment_id in the comment_votes table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
}