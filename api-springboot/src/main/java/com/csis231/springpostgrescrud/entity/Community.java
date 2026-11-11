package com.csis231.springpostgrescrud.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a community that clients can create, join, and post in.
 *
 * Maps to the communities table. Every community has one creator (client)
 * who owns it and can edit or delete it. Any client may join as a member. The creator is
 * automatically added as the first member on creation.
 *
 * The many-to-many relationship with Client for membership is managed through
 * the community_members join table, owned by this entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "communities")
public class Community {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The unique name of the community
     * Must be unique across all communities.
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * An optional text description of the community's purpose and rules.
     * Stored as TEXT to support long descriptions.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * The timestamp when the community was created.
     */
    @CreationTimestamp
    private Instant createdAt;

    /**
     * The client who created and owns this community. Required.
     * Foreign key: creator_id in the communities table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Client creator;

    /**
     * All posts submitted to this community.
     */
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    /**
     * All clients who are members of this community.
     * Managed through the community_members join table.
     * This is the owning side of the ManyToMany — this entity controls the join table.
     */
    @ManyToMany
    @JoinTable(
            name = "community_members",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<Client> members = new ArrayList<>();
}