package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.CommentVoteDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Comment;
import com.csis231.springpostgrescrud.entity.CommentVote;
import com.csis231.springpostgrescrud.entity.VoteType;
import com.csis231.springpostgrescrud.exception.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.CommentVoteMapper;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import com.csis231.springpostgrescrud.repository.CommentRepository;
import com.csis231.springpostgrescrud.repository.CommentVoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link CommentVoteService} handling comment vote logic.
 * Updates the cached voteCount on the associated comment.
 */
@Service
@AllArgsConstructor
public class CommentVoteServiceImpl implements CommentVoteService {

    private final CommentVoteRepository commentVoteRepository;
    private final CommentRepository commentRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public CommentVoteDto castVote(Long commentId, Long clientId, VoteType voteType) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        Optional<CommentVote> existingVote = commentVoteRepository.findByCommentIdAndClientId(commentId, clientId);
        CommentVote vote;

        if (existingVote.isPresent()) {
            vote = existingVote.get();
            int oldDelta = vote.getVoteType().getType();
            int newDelta = voteType.getType();
            comment.setVoteCount(comment.getVoteCount() - oldDelta + newDelta);
            vote.setVoteType(voteType);
        } else {
            vote = new CommentVote();
            vote.setComment(comment);
            vote.setClient(client);
            vote.setVoteType(voteType);
            comment.setVoteCount(comment.getVoteCount() + voteType.getType());
        }

        CommentVote saved = commentVoteRepository.save(vote);
        commentRepository.save(comment);
        return CommentVoteMapper.toDto(saved);
    }

    @Override
    public CommentVoteDto getVote(Long commentId, Long clientId) {
        return commentVoteRepository.findByCommentIdAndClientId(commentId, clientId)
                .map(CommentVoteMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteVote(Long commentId, Long clientId) {
        CommentVote vote = commentVoteRepository.findByCommentIdAndClientId(commentId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found for comment " + commentId + " and client " + clientId));
        Comment comment = vote.getComment();
        comment.setVoteCount(comment.getVoteCount() - vote.getVoteType().getType());
        commentRepository.save(comment);
        commentVoteRepository.delete(vote);
    }
}