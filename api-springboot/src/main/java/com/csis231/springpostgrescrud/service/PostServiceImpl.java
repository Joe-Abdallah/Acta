package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.PostDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Community;
import com.csis231.springpostgrescrud.entity.Post;
import com.csis231.springpostgrescrud.exception.BadRequestException;
import com.csis231.springpostgrescrud.exception.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.PostMapper;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import com.csis231.springpostgrescrud.repository.CommunityRepository;
import com.csis231.springpostgrescrud.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;


/**
 * Implementation of {@link PostService} handling post business logic.
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ClientRepository clientRepository;
    private final CommunityRepository communityRepository;
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private static final String GEMINI_MODEL = "gemini-2.5-flash-lite";

    @Override
    public String summarizePostContent(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        String content = post.getContent();
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Post has no content to summarize.");
        }

        try {
            // Create a Gemini client
            com.google.genai.Client geminiClient = com.google.genai.Client.builder()
                    .apiKey(geminiApiKey)
                    .build();

            // prompt
            String prompt = "Summarize the following text:\n\n" + content;

            // API call
            GenerateContentResponse response = geminiClient.models.generateContent(GEMINI_MODEL, prompt, null);

            // Return the summary text
            return response.text();
        } catch (Exception e) {
            throw new RuntimeException("Failed to summarize post: " + e.getMessage(), e);
        }
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        if (postDto == null) throw new BadRequestException("Post DTO cannot be null");
        if (postDto.getTitle() == null || postDto.getTitle().isBlank()) {
            throw new BadRequestException("Post title is required");
        }

        Client author = clientRepository.findById(postDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + postDto.getAuthorId()));
        Community community = communityRepository.findById(postDto.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + postDto.getCommunityId()));

        Post post = PostMapper.toEntity(postDto, author, community);
        Post saved = postRepository.save(post);
        return PostMapper.toDto(saved);
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return PostMapper.toDto(post);
    }

    @Override
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        if (postDto.getTitle() != null && !postDto.getTitle().isBlank()) {
            post.setTitle(postDto.getTitle());
        }
        if (postDto.getContent() != null) {
            post.setContent(postDto.getContent());
        }

        Post updated = postRepository.save(post);
        return PostMapper.toDto(updated);
    }

    @Override
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
}