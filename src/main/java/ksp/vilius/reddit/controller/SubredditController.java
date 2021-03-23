package ksp.vilius.reddit.controller;

import ksp.vilius.reddit.dto.SubredditDto;
import ksp.vilius.reddit.service.SubredditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubredditController {

    private final SubredditService subredditService;

    @PostMapping
    public ResponseEntity<SubredditDto> createSubreddit(@RequestBody SubredditDto subredditDto) {

        return new ResponseEntity<>(subredditService.createSubreddit(subredditDto), HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<SubredditDto>> getAllSubreddit() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(subredditService.getAllSubreddits());
    }
}
