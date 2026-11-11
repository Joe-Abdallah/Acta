package com.csis231.springpostgrescrud.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a comment on a post, or a reply to another comment.
 *
 * Maps to the comments table. Comments support unlimited nesting through
 * a self-referential relationship: each comment optionally has a parentComment.
 * Top-level comments have parentComment = null. Replies have parentComment
 * pointing to the comment they reply to.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The text content of the comment. Required.
     * Stored as TEXT to support long-form replies.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * The timestamp when the comment was created.
     */
    @CreationTimestamp
    private Instant createdAt;

    /**
     * Cached count of net votes (upvotes minus downvotes) on this comment.
     * Maintained by CommentVoteService. Initialized to 0 on creation.
     */
    private Integer voteCount = 0;

    /**
     * The client who wrote this comment. Required.
     * Foreign key: client_id in the comments table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client author;

    /**
     * The post this comment belongs to. Required.
     * Foreign key: post_id} in the comments table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * The parent comment, if this is a reply. Null for top-level comments.
     * Foreign key: parent_comment_id in the comments table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    /**
     * The direct replies to this comment.
     * This is the inverse side of the self-referential relationship,
     * mapped by the parentComment field above.
     */
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    /**
     * All votes cast on this comment.
     */
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentVote> votes = new ArrayList<>();
}