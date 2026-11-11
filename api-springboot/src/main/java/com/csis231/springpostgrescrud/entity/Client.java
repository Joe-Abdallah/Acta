package com.csis231.springpostgrescrud.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered user of the application.
 *
 * Maps to the clients table. A client is the central actor in the system —
 * they are authors of posts and comments, cast votes, create communities, and join communities.
 * All owned entities (posts, comments, votes, communities) are cascade-deleted when
 * the user is deleted.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The client's public display name. Must be unique across all clients.
     * Used for login and public profile URLs.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * The client's email address. Must be unique across all clients.
     * Used for registration.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The client's hashed password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The timestamp when the user account was created.
     */
    @CreationTimestamp
    private Instant createdAt;

    /**
     * All posts authored by this client.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    /**
     * All comments written by this client.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    /**
     * All post votes cast by this client.
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostVote> postVotes = new ArrayList<>();

    /**
     * All comment votes cast by this client.
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentVote> commentVotes = new ArrayList<>();

    /**
     * All communities created (owned) by this client.
     */
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> createdCommunities = new ArrayList<>();

    /**
     * All communities this client has joined as a member.
     */
    @ManyToMany(mappedBy = "members")
    private List<Community> joinedCommunities = new ArrayList<>();
}