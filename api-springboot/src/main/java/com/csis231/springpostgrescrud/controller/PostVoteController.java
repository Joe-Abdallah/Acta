package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.PostVoteDto;
import com.csis231.springpostgrescrud.entity.VoteType;
import com.csis231.springpostgrescrud.service.PostVoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing votes on posts.
 */
@RestController
@RequestMapping("/api/post-votes")
@AllArgsConstructor
public class PostVoteController {

    private final PostVoteService postVoteService;

    @PostMapping
    public ResponseEntity<PostVoteDto> castVote(@RequestParam Long postId,
                                                @RequestParam Long clientId,
                                                @RequestParam VoteType voteType) {
        PostVoteDto vote = postVoteService.castVote(postId, clientId, voteType);
        return new ResponseEntity<>(vote, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PostVoteDto> getVote(@RequestParam Long postId,
                                               @RequestParam Long clientId) {
        PostVoteDto vote = postVoteService.getVote(postId, clientId);
        return vote != null ? ResponseEntity.ok(vote) : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVote(@RequestParam Long postId,
                                           @RequestParam Long clientId) {
        postVoteService.deleteVote(postId, clientId);
        return ResponseEntity.noContent().build();
    }
}