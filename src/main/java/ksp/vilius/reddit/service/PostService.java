package ksp.vilius.reddit.service;

import ksp.vilius.reddit.dto.PostRequest;
import ksp.vilius.reddit.dto.PostResponse;
import ksp.vilius.reddit.exceptions.SpringRedditException;
import ksp.vilius.reddit.exceptions.SubredditNotFoundException;
import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.model.Subreddit;
import ksp.vilius.reddit.model.User;
import ksp.vilius.reddit.repositories.PostRepository;
import ksp.vilius.reddit.repositories.SubredditRepository;
import ksp.vilius.reddit.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void createNewPost(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName());
        User currentUser = authService.getCurrentUser();

        Post post = modelMapper.map(postRequest, Post.class);
        post.setSubreddit(subreddit);
        post.setUser(currentUser);
        postRepository.save(post);

    }


    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {

        return postRepository.findAll()
                .stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SubredditNotFoundException("No such subreddit"));
        List<Post> listOfPosts = postRepository.findAllBySubreddit(subreddit);
        return listOfPosts.stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No such post with that id"));

        return modelMapper.map(post, PostResponse.class);

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String name) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("No user was found with such username " + name));

        return postRepository.findByUser(user)
                .stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList());
    }
}
