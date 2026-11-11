package com.csis231.javafxclient.model;

import java.time.Instant;

public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private Integer voteCount;
    private Integer commentCount;
    private Long authorId;
    private Long communityId;
    private VoteType userVote; // optional, can be null

    public PostDto() {}

    public PostDto(Long id, String title, String content, Instant createdAt,
                   Integer voteCount, Integer commentCount, Long authorId,
                   Long communityId, VoteType userVote) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.voteCount = voteCount;
        this.commentCount = commentCount;
        this.authorId = authorId;
        this.communityId = communityId;
        this.userVote = userVote;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Integer getVoteCount() { return voteCount; }
    public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }

    public VoteType getUserVote() { return userVote; }
    public void setUserVote(VoteType userVote) { this.userVote = userVote; }
}