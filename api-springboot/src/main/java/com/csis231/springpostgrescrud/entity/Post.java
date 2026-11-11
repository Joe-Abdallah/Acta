package com.csis231.springpostgrescrud.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a post submitted to a community.
 * Maps to the posts table. A post belongs to exactly one Community
 * and one author (client).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The post title. Required. Displayed as the headline of the post.
     */
    @Column(nullable = false)
    private String title;

    /**
     * The text body of the post. Optional
     * Stored as TEXT to support long-form content.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * The timestamp when the post was created.
     */
    @CreationTimestamp
    private Instant createdAt;

    /**
     * Cached count of net votes (upvotes minus downvotes) on this post.
     * Maintained by PostVoteService, incremented or decremented on every vote action.
     */
    private Integer voteCount = 0;

    /**
     * Cached count of total comments on this post.
     * Maintained by CommentService — incremented on comment creation,
     * decremented on comment deletion. Initialized to 0 on creation.
     */
    private Integer commentCount = 0;

    /**
     * The client who created this post. Required.
     * Foreign key: client_id in the posts table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client author;

    /**
     * The community this post was submitted to. Required.
     * Foreign key: community_id in the posts table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    /**
     * All comments on this post.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    /**
     * All votes cast on this post.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostVote> votes = new ArrayList<>();
}