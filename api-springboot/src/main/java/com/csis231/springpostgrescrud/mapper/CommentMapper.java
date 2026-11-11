package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.CommentDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Comment;
import com.csis231.springpostgrescrud.entity.Post;
import com.csis231.springpostgrescrud.entity.VoteType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto toDto(Comment comment, Map<Long, VoteType> userVoteMap) {
        if (comment == null) return null;
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getVoteCount(),
                comment.getAuthor().getId(),
                comment.getPost().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                null,
                userVoteMap != null ? userVoteMap.get(comment.getId()) : null
        );
    }

    public static CommentDto toDtoWithReplies(Comment comment, Map<Long, VoteType> userVoteMap) {
        if (comment == null) return null;
        List<CommentDto> replyDtos = comment.getReplies() == null
                ? Collections.emptyList()
                : comment.getReplies().stream()
                .map(reply -> toDtoWithReplies(reply, userVoteMap))
                .collect(Collectors.toList());

        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getVoteCount(),
                comment.getAuthor().getId(),
                comment.getPost().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                replyDtos,
                userVoteMap != null ? userVoteMap.get(comment.getId()) : null
        );
    }

    public static Comment toEntity(CommentDto dto, Client author, Post post, Comment parentComment) {
        if (dto == null) return null;
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setVoteCount(0);
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setParentComment(parentComment);
        return comment;
    }
}