package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.CommentDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Comment;
import com.csis231.springpostgrescrud.entity.Post;
import com.csis231.springpostgrescrud.exception.BadRequestException;
import com.csis231.springpostgrescrud.exception.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.CommentMapper;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import com.csis231.springpostgrescrud.repository.CommentRepository;
import com.csis231.springpostgrescrud.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Implementation of {@link CommentService} handling comment business logic.
 */
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ClientRepository clientRepository;
    private final PostRepository postRepository;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        if (commentDto == null) throw new BadRequestException("Comment DTO cannot be null");
        if (commentDto.getContent() == null || commentDto.getContent().isBlank()) {
            throw new BadRequestException("Comment content cannot be empty");
        }

        Client author = clientRepository.findById(commentDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + commentDto.getAuthorId()));
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + commentDto.getPostId()));

        Comment parent = null;
        if (commentDto.getParentCommentId() != null) {
            parent = commentRepository.findById(commentDto.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + commentDto.getParentCommentId()));
        }

        Comment comment = CommentMapper.toEntity(commentDto, author, post, parent);
        Comment saved = commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return CommentMapper.toDto(saved, Collections.emptyMap());
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return CommentMapper.toDtoWithReplies(comment, Collections.emptyMap());
    }

    @Override
    public List<CommentDto> getCommentsByPost(Long postId) {
        List<Comment> roots = commentRepository.findByPost_IdAndParentCommentIsNull(postId);
        return roots.stream()
                // toDtoWithReplies recursively populates the replies field
                .map(c -> CommentMapper.toDtoWithReplies(c, Collections.emptyMap()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long id, String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new BadRequestException("Comment content cannot be empty");
        }
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        comment.setContent(newContent);
        Comment updated = commentRepository.save(comment);
        return CommentMapper.toDtoWithReplies(updated, Collections.emptyMap());
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        Long postId = comment.getPost().getId();

        commentRepository.delete(comment);

        int newCommentCount = (int) commentRepository.countByPostId(postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        post.setCommentCount(newCommentCount);
        postRepository.save(post);
    }
}