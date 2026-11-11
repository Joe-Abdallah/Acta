package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.PostVoteDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.PostVote;
import com.csis231.springpostgrescrud.entity.Post;

public class PostVoteMapper {

    public static PostVoteDto toDto(PostVote vote) {
        if (vote == null) return null;
        return new PostVoteDto(
                vote.getId(),
                vote.getVoteType(),
                vote.getClient().getId(),
                vote.getPost().getId()
        );
    }

    public static PostVote toEntity(PostVoteDto dto, Client user, Post post) {
        if (dto == null) return null;
        PostVote vote = new PostVote();
        vote.setVoteType(dto.getVoteType());
        vote.setClient(user);
        vote.setPost(post);
        return vote;
    }
}