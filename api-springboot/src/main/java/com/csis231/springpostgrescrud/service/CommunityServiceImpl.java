package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.CommunityDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Community;
import com.csis231.springpostgrescrud.exception.BadRequestException;
import com.csis231.springpostgrescrud.exception.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.CommunityMapper;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import com.csis231.springpostgrescrud.repository.CommunityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CommunityService} handling community business logic.
 */
@Service
@AllArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final ClientRepository clientRepository;

    @Override
    public CommunityDto createCommunity(CommunityDto communityDto) {
        if (communityDto == null) throw new BadRequestException("Community DTO cannot be null");
        if (communityDto.getName() == null || communityDto.getName().isBlank()) {
            throw new BadRequestException("Community name is required");
        }
        if (communityRepository.existsByNameIgnoreCase(communityDto.getName().trim())) {
            throw new BadRequestException("Community name already exists: " + communityDto.getName());
        }

        Client creator = clientRepository.findById(communityDto.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with id: " + communityDto.getCreatorId()));

        Community community = CommunityMapper.toEntity(communityDto, creator);
        community.getMembers().add(creator); // creator is first member
        Community saved = communityRepository.save(community);
        return CommunityMapper.toDto(saved, saved.getMembers().size());
    }

    @Override
    public CommunityDto getCommunityById(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + id));
        return CommunityMapper.toDto(community, community.getMembers().size());
    }

    @Override
    public List<CommunityDto> getAllCommunities() {
        return communityRepository.findAll().stream()
                .map(c -> CommunityMapper.toDto(c, c.getMembers().size()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommunityDto updateCommunity(Long id, String newDescription) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + id));
        community.setDescription(newDescription);
        Community updated = communityRepository.save(community);
        return CommunityMapper.toDto(updated, updated.getMembers().size());
    }

    @Override
    public void deleteCommunity(Long id) {
        if (!communityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Community not found with id: " + id);
        }
        communityRepository.deleteById(id);
    }
}