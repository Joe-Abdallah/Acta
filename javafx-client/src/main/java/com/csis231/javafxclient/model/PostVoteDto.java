package com.csis231.javafxclient.model;

public class PostVoteDto {
    private Long id;
    private VoteType voteType;
    private Long userId;
    private Long postId;

    public PostVoteDto() {}

    public PostVoteDto(Long id, VoteType voteType, Long userId, Long postId) {
        this.id = id;
        this.voteType = voteType;
        this.userId = userId;
        this.postId = postId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VoteType getVoteType() { return voteType; }
    public void setVoteType(VoteType voteType) { this.voteType = voteType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
}