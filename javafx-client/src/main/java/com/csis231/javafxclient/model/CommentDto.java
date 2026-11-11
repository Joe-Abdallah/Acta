package com.csis231.javafxclient.model;

import java.time.Instant;
import java.util.List;

public class CommentDto {
    private Long id;
    private String content;
    private Instant createdAt;
    private Integer voteCount;
    private Long authorId;
    private Long postId;
    private Long parentCommentId;
    private List<CommentDto> replies;
    private VoteType userVote;

    public CommentDto() {}

    public CommentDto(Long id, String content, Instant createdAt, Integer voteCount,
                      Long authorId, Long postId, Long parentCommentId,
                      List<CommentDto> replies, VoteType userVote) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.voteCount = voteCount;
        this.authorId = authorId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.replies = replies;
        this.userVote = userVote;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Integer getVoteCount() { return voteCount; }
    public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }

    public List<CommentDto> getReplies() { return replies; }
    public void setReplies(List<CommentDto> replies) { this.replies = replies; }

    public VoteType getUserVote() { return userVote; }
    public void setUserVote(VoteType userVote) { this.userVote = userVote; }
}