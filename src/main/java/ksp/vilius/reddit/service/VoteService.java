package ksp.vilius.reddit.service;

import ksp.vilius.reddit.dto.VoteDto;
import ksp.vilius.reddit.exceptions.PostNotFoundException;
import ksp.vilius.reddit.exceptions.SpringRedditException;
import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.model.Vote;
import ksp.vilius.reddit.model.VoteType;
import ksp.vilius.reddit.repositories.PostRepository;
import ksp.vilius.reddit.repositories.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;


    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + voteDto.getPostId()));

        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            throw new SpringRedditException("you have already voted");
        }
        if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        Vote vote = modelMapper.map(voteDto, Vote.class);
        vote.setPost(post);
        vote.setUser(authService.getCurrentUser());
        voteRepository.save(vote);
        postRepository.save(post);

    }
}
