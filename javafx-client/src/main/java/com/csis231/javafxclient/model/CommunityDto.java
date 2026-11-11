package com.csis231.javafxclient.model;

import java.time.Instant;

public class CommunityDto {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Long creatorId;
    private Integer memberCount;

    public CommunityDto() {}

    public CommunityDto(Long id, String name, String description, Instant createdAt, Long creatorId, Integer memberCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.creatorId = creatorId;
        this.memberCount = memberCount;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
}