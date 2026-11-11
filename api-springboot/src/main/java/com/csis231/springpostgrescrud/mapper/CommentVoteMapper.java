package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.CommentVoteDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.CommentVote;
import com.csis231.springpostgrescrud.entity.Comment;

public class CommentVoteMapper {

    public static CommentVoteDto toDto(CommentVote vote) {
        if (vote == null) return null;
        return new CommentVoteDto(
                vote.getId(),
                vote.getVoteType(),
                vote.getClient().getId(),
                vote.getComment().getId()
        );
    }

    public static CommentVote toEntity(CommentVoteDto dto, Client user, Comment comment) {
        if (dto == null) return null;
        CommentVote vote = new CommentVote();
        vote.setVoteType(dto.getVoteType());
        vote.setClient(user);
        vote.setComment(comment);
        return vote;
    }

}