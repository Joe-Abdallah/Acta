package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.CommunityDto;
import com.csis231.springpostgrescrud.entity.Community;
import com.csis231.springpostgrescrud.entity.Client;

public class CommunityMapper {

    public static CommunityDto toDto(Community community, int memberCount) {
        if (community == null) return null;
        return new CommunityDto(
                community.getId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt(),
                community.getCreator().getId(),
                memberCount
        );
    }

    public static Community toEntity(CommunityDto dto, Client creator) {
        if (dto == null) return null;
        Community community = new Community();
        community.setName(dto.getName());
        community.setDescription(dto.getDescription());
        community.setCreator(creator);
        return community;
    }
}