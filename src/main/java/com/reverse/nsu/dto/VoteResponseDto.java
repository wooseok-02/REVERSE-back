package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Vote;
import com.reverse.nsu.entity.VoteOption;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class VoteResponseDto {

    private final Integer voteId;
    private final String userId;
    private final String title;
    private final String content;
    private final LocalDateTime deadline;
    private final Boolean isMultiple;
    private final Boolean isClosed;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final List<OptionDto> options;

    // 내가 투표한 optionId (미로그인 시 null)
    private final Integer myVotedOptionId;

    public VoteResponseDto(Vote vote, Integer myVotedOptionId) {
        this.voteId = vote.getVoteId();
        this.userId = vote.getUserId();
        this.title = vote.getTitle();
        this.content = vote.getContent();
        this.deadline = vote.getDeadline();
        this.isMultiple = vote.getIsMultiple();
        this.isClosed = vote.isClosed();
        this.createdDate = vote.getCreatedDate();
        this.modifiedDate = vote.getModifiedDate();
        this.options = vote.getOptions().stream()
                .map(OptionDto::new)
                .collect(Collectors.toList());
        this.myVotedOptionId = myVotedOptionId;
    }

    @Getter
    public static class OptionDto {
        private final Integer optionId;
        private final String optionText;
        private final Integer sortOrder;
        private final Integer voteCount;

        public OptionDto(VoteOption option) {
            this.optionId = option.getOptionId();
            this.optionText = option.getOptionText();
            this.sortOrder = option.getSortOrder();
            this.voteCount = option.getVoteCount();
        }
    }
}
