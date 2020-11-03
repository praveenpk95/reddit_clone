package com.project.backend.subreddit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubRedditService {

    private final SubredditRepository subredditRepository;

    @Transactional
    public SubRedditDto save(SubRedditDto subRedditDto) {
       Subreddit subreddit = mapSubbredditDto(subRedditDto);
       subredditRepository.save(subreddit);

        return subRedditDto;
    }

    private Subreddit mapSubbredditDto(SubRedditDto subRedditDto) {
        return Subreddit.builder().name(subRedditDto.getName())
                .description(subRedditDto.getDescription())
                .build();
    }

    @Transactional
    List<SubRedditDto> getAll() {

        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    private SubRedditDto  mapToDto(Subreddit subreddit) {
        return SubRedditDto.builder().name(subreddit.getName())
                .id(subreddit.getId())
                .numberOfPosts(subreddit.getPosts().size())
                .build();
    }

}
