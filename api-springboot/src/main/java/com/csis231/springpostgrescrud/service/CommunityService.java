package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.CommunityDto;

import java.util.List;

/**
 * Service interface for managing {@link com.csis231.springpostgrescrud.entity.Community} entities.
 * Provides CRUD plus member count retrieval.
 */
public interface CommunityService {

    /**
     * Creates a new community. The creator is automatically added as a member.
     *
     * @param communityDto DTO containing name, description, and creatorId
     * @return the created community DTO (memberCount = 1)
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if creator not found
     * @throws com.csis231.springpostgrescrud.exception.BadRequestException        if community name already exists
     */
    CommunityDto createCommunity(CommunityDto communityDto);

    /**
     * Retrieves a community by its ID.
     *
     * @param id the community ID
     * @return the community DTO including current member count
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if not found
     */
    CommunityDto getCommunityById(Long id);

    /**
     * Retrieves all communities.
     * @return a list of community DTOs
     */
    List<CommunityDto> getAllCommunities();

    /**
     * Updates a community's description. Name cannot be changed to avoid URL breakage.
     *
     * @param id              the community ID
     * @param newDescription  the new description
     * @return the updated community DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if community not found
     */
    CommunityDto updateCommunity(Long id, String newDescription);

    /**
     * Deletes a community by ID. All posts and comments are cascade‑deleted.
     *
     * @param id the community ID
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if community not found
     */
    void deleteCommunity(Long id);
}