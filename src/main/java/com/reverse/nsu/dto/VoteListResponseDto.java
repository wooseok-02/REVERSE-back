package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Vote;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VoteListResponseDto {

    private final Integer voteId;
    private final String userId;
    private final String title;
    private final LocalDateTime deadline;
    private final Boolean isClosed;
    private final int optionCount;
    private final int totalVoteCount;
    private final LocalDateTime createdDate;

    public VoteListResponseDto(Vote vote) {
        this.voteId = vote.getVoteId();
        this.userId = vote.getUserId();
        this.title = vote.getTitle();
        this.deadline = vote.getDeadline();
        this.isClosed = vote.isClosed();
        this.optionCount = vote.getOptions().size();
        this.totalVoteCount = vote.getOptions().stream()
                .mapToInt(o -> o.getVoteCount())
                .sum();
        this.createdDate = vote.getCreatedDate();
    }
}
