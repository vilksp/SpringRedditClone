package ksp.vilius.reddit.service;

import ksp.vilius.reddit.dto.SubredditDto;
import ksp.vilius.reddit.exceptions.SpringRedditException;
import ksp.vilius.reddit.model.Subreddit;
import ksp.vilius.reddit.repositories.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;

    @Transactional
    public SubredditDto createSubreddit(SubredditDto subredditDto) {
        Subreddit save = subredditRepository.save(mapSubredditDto(subredditDto));
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
        return Subreddit.builder()
                .name(subredditDto.getSubredditName())
                .description(subredditDto.getDescription())
                .build();

    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAllSubreddits() {

        return subredditRepository
                .findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SubredditDto mapToDto(Subreddit subreddit) {
        return SubredditDto.builder()
                .id(subreddit.getId())
                .subredditName(subreddit.getName())
                .description(subreddit.getDescription())
                .build();
    }

    public SubredditDto getSubredditById(Long id) {
        Subreddit subreddit = subredditRepository
                .findById(id)
                .orElseThrow(
                        () -> new SpringRedditException("No subreddit found with such id: " + id));

        return mapToDto(subreddit);

    }
}
