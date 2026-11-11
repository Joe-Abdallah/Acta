package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.CommunityDto;
import com.csis231.springpostgrescrud.service.CommunityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link com.csis231.springpostgrescrud.entity.Community} entities.
 */
@RestController
@RequestMapping("/api/communities")
@AllArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<CommunityDto> createCommunity(@RequestBody CommunityDto communityDto) {
        CommunityDto created = communityService.createCommunity(communityDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> getCommunityById(@PathVariable Long id) {
        CommunityDto community = communityService.getCommunityById(id);
        return ResponseEntity.ok(community);
    }

    @GetMapping
    public ResponseEntity<List<CommunityDto>> getAllCommunities() {
        List<CommunityDto> communities = communityService.getAllCommunities();
        return ResponseEntity.ok(communities);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommunityDto> updateCommunity(@PathVariable Long id, @RequestBody String newDescription) {
        CommunityDto updated = communityService.updateCommunity(id, newDescription);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }
}