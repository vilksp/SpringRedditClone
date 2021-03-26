package ksp.vilius.reddit.service;

import ksp.vilius.reddit.dto.CommentsDto;
import ksp.vilius.reddit.exceptions.PostNotFoundException;
import ksp.vilius.reddit.model.Comment;
import ksp.vilius.reddit.model.NotificationEmail;
import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.model.User;
import ksp.vilius.reddit.repositories.CommentRepository;
import ksp.vilius.reddit.repositories.PostRepository;
import ksp.vilius.reddit.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final MailContentBuilder mailContentBuilder;
    private final UserRepository userRepository;
    private final MailService mailService;

    public void createNewComment(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId()).orElseThrow(
                () -> new PostNotFoundException("There was no post found with such id: " + commentsDto.getPostId()));

        User user = authService.getCurrentUser();

        Comment comment = modelMapper.map(commentsDto, Comment.class);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedDate(Instant.now());
        log.info("new comment was created");
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted comment on your post");
        mailService.sendEmail(new NotificationEmail(user.getUsername() + "Comment on your post", user.getEmail(), message));
    }

    public List<CommentsDto> getAllPostComments(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("post doesn't exist with such id"));
        List<Comment> commentListForPost = commentRepository.findByPost(post);
        return commentListForPost
                .stream()
                .map(comment -> {
                    CommentsDto dto = modelMapper.map(comment, CommentsDto.class);
                    dto.setUserName(comment.getUser().getUsername());
                    dto.setPostId(comment.getPost().getPostId());

                    return dto;
                })
                .collect(Collectors.toList());

    }

    public List<CommentsDto> getAllPostCommentsForUser(String username) {
        User userToFind = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user was found with such username: " + username));

        return commentRepository.findAllByUser(userToFind)
                .stream()
                .map(comment -> {
                    CommentsDto dto = modelMapper.map(comment, CommentsDto.class);
                    dto.setUserName(comment.getUser().getUsername());
                    dto.setCreatedDate(comment.getCreatedDate());
                    dto.setPostId(comment.getPost().getPostId());

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
