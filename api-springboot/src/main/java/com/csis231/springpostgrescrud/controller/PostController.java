package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.PostDto;
import com.csis231.springpostgrescrud.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link com.csis231.springpostgrescrud.entity.Post} entities.
 */
@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/{id}/summarize")
    public ResponseEntity<String> summarizePost(@PathVariable Long id) {
        String summary = postService.summarizePostContent(id);
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        PostDto created = postService.createPost(postDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        PostDto updated = postService.updatePost(id, postDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}