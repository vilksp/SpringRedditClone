package ksp.vilius.reddit.controller;

import ksp.vilius.reddit.dto.CommentsDto;
import ksp.vilius.reddit.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) {

        commentService.createNewComment(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllComments(@PathVariable Long postId) {

        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllPostComments(postId));
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String username) {

        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllPostCommentsForUser(username));
    }

}
