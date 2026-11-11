package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.CommentVoteDto;
import com.csis231.springpostgrescrud.entity.VoteType;
import com.csis231.springpostgrescrud.service.CommentVoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing votes on comments.
 */
@RestController
@RequestMapping("/api/comment-votes")
@AllArgsConstructor
public class CommentVoteController {

    private final CommentVoteService commentVoteService;

    @PostMapping
    public ResponseEntity<CommentVoteDto> castVote(@RequestParam Long commentId,
                                                   @RequestParam Long clientId,
                                                   @RequestParam VoteType voteType) {
        CommentVoteDto vote = commentVoteService.castVote(commentId, clientId, voteType);
        return new ResponseEntity<>(vote, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CommentVoteDto> getVote(@RequestParam Long commentId,
                                                  @RequestParam Long clientId) {
        CommentVoteDto vote = commentVoteService.getVote(commentId, clientId);
        return vote != null ? ResponseEntity.ok(vote) : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVote(@RequestParam Long commentId,
                                           @RequestParam Long clientId) {
        commentVoteService.deleteVote(commentId, clientId);
        return ResponseEntity.noContent().build();
    }
}