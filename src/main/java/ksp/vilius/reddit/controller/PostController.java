package ksp.vilius.reddit.controller;

import ksp.vilius.reddit.dto.PostRequest;
import ksp.vilius.reddit.dto.PostResponse;
import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Slf4j
@AllArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostRequest postRequest) {

        postService.createNewPost(postRequest);
        return new ResponseEntity("Post created successfully", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {

        return new ResponseEntity(postService.getAllPosts(), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {

        return new ResponseEntity(postService.getPostById(id), HttpStatus.OK);
    }

    @GetMapping("/by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@PathVariable Long id) {

        return new ResponseEntity(postService.getPostsBySubreddit(id), HttpStatus.OK);
    }

    @GetMapping("/by-user/{name}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String name) {


        return new ResponseEntity(postService.getPostsByUsername(name), HttpStatus.OK);
    }

}
