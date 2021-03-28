package ksp.vilius.reddit.service;


import com.github.marlonlom.utilities.timeago.TimeAgo;
import ksp.vilius.reddit.dto.PostRequest;
import ksp.vilius.reddit.dto.PostResponse;
import ksp.vilius.reddit.exceptions.SpringRedditException;
import ksp.vilius.reddit.exceptions.SubredditNotFoundException;
import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.model.Subreddit;
import ksp.vilius.reddit.model.User;
import ksp.vilius.reddit.repositories.CommentRepository;
import ksp.vilius.reddit.repositories.PostRepository;
import ksp.vilius.reddit.repositories.SubredditRepository;
import ksp.vilius.reddit.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public void createNewPost(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName());
        User currentUser = authService.getCurrentUser();

        Post post = modelMapper.map(postRequest, Post.class);
        post.setSubreddit(subreddit);
        post.setUser(currentUser);
        post.setCreatedDate(Instant.now());
        postRepository.save(post);

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {

        return postRepository.findAll()
                .stream()
                .map(post -> {
                    PostResponse postResponse = modelMapper.map(post, PostResponse.class);
                    postResponse.setVoteCount(commentRepository.findByPost(post).size());
                    postResponse.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
                    return postResponse;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SubredditNotFoundException("No such subreddit"));
        List<Post> listOfPosts = postRepository.findAllBySubreddit(subreddit);
        return listOfPosts.stream()
                .map(post -> {
                    PostResponse postResponse = modelMapper.map(post, PostResponse.class);
                    postResponse.setVoteCount(commentRepository.findByPost(post).size());
                    postResponse.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
                    return postResponse;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No such post with that id"));

        PostResponse postResponse = modelMapper.map(post, PostResponse.class);
        postResponse.setCommentCount(commentRepository.findByPost(post).size());
        postResponse.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
        postResponse.setCommentCount(commentRepository.findByPost(post).size());
        return postResponse;

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String name) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("No user was found with such username " + name));

        return postRepository.findByUser(user)
                .stream()
                .map(post -> {
                    PostResponse postRes = modelMapper.map(post, PostResponse.class);
                    postRes.setVoteCount(commentRepository.findByPost(post).size());
                    postRes.setDuration(TimeAgo.using(post.getCreatedDate().toEpochMilli()));
                    postRes.setCommentCount(commentRepository.findByPost(post).size());
                    return postRes;
                })
                .collect(Collectors.toList());
    }
}
