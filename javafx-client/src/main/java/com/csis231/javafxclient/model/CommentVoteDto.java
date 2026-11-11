package com.csis231.javafxclient.model;

public class CommentVoteDto {
    private Long id;
    private VoteType voteType;
    private Long userId;
    private Long commentId;

    public CommentVoteDto() {}

    public CommentVoteDto(Long id, VoteType voteType, Long userId, Long commentId) {
        this.id = id;
        this.voteType = voteType;
        this.userId = userId;
        this.commentId = commentId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VoteType getVoteType() { return voteType; }
    public void setVoteType(VoteType voteType) { this.voteType = voteType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
}