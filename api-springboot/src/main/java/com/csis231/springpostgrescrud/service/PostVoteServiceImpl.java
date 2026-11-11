package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.PostVoteDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Post;
import com.csis231.springpostgrescrud.entity.PostVote;
import com.csis231.springpostgrescrud.entity.VoteType;
import com.csis231.springpostgrescrud.exception.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.PostVoteMapper;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import com.csis231.springpostgrescrud.repository.PostRepository;
import com.csis231.springpostgrescrud.repository.PostVoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link PostVoteService} handling post vote logic.
 * Updates the cached voteCount on the associated post.
 */
@Service
@AllArgsConstructor
public class PostVoteServiceImpl implements PostVoteService {

    private final PostVoteRepository postVoteRepository;
    private final PostRepository postRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public PostVoteDto castVote(Long postId, Long clientId, VoteType voteType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        Optional<PostVote> existingVote = postVoteRepository.findByPostIdAndClientId(postId, clientId);
        PostVote vote;

        if (existingVote.isPresent()) {
            vote = existingVote.get();
            // Adjust post vote count: remove old vote's contribution, then add new
            int oldDelta = vote.getVoteType().getType();
            int newDelta = voteType.getType();
            post.setVoteCount(post.getVoteCount() - oldDelta + newDelta);
            vote.setVoteType(voteType);
        } else {
            vote = new PostVote();
            vote.setPost(post);
            vote.setClient(client);
            vote.setVoteType(voteType);
            post.setVoteCount(post.getVoteCount() + voteType.getType());
        }

        PostVote saved = postVoteRepository.save(vote);
        postRepository.save(post); // persist updated voteCount
        return PostVoteMapper.toDto(saved);
    }

    @Override
    public PostVoteDto getVote(Long postId, Long clientId) {
        return postVoteRepository.findByPostIdAndClientId(postId, clientId)
                .map(PostVoteMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteVote(Long postId, Long clientId) {
        PostVote vote = postVoteRepository.findByPostIdAndClientId(postId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found for post " + postId + " and client " + clientId));
        Post post = vote.getPost();
        post.setVoteCount(post.getVoteCount() - vote.getVoteType().getType());
        postRepository.save(post);
        postVoteRepository.delete(vote);
    }
}