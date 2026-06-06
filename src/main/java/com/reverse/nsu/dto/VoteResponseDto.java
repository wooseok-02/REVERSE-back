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
    private final Boolean isSecret;
    private final Integer participantRole;
    private final Integer resultViewRole;
    private final Boolean isClosed;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final List<OptionDto> options;

    /** 내가 투표한 optionId (미로그인 또는 미투표 시 null) */
    private final Integer myVotedOptionId;

    /**
     * @param canViewResult true 이면 각 option의 voteCount를 실제 값으로 반환,
     *                      false 이면 voteCount를 null로 숨겨 반환
     */
    public VoteResponseDto(Vote vote, Integer myVotedOptionId, boolean canViewResult) {
        this.voteId = vote.getVoteId();
        this.userId = vote.getUserId();
        this.title = vote.getTitle();
        this.content = vote.getContent();
        this.deadline = vote.getDeadline();
        this.isMultiple = vote.getIsMultiple();
        this.isSecret = vote.getIsSecret();
        this.participantRole = vote.getParticipantRole();
        this.resultViewRole = vote.getResultViewRole();
        this.isClosed = vote.isClosed();
        this.createdDate = vote.getCreatedDate();
        this.modifiedDate = vote.getModifiedDate();
        this.options = vote.getOptions().stream()
                .map(opt -> new OptionDto(opt, canViewResult))
                .collect(Collectors.toList());
        this.myVotedOptionId = myVotedOptionId;
    }

    @Getter
    public static class OptionDto {
        private final Integer optionId;
        private final String optionText;
        private final Integer sortOrder;
        /** 결과 조회 권한이 없으면 null */
        private final Integer voteCount;

        public OptionDto(VoteOption option, boolean canViewResult) {
            this.optionId = option.getOptionId();
            this.optionText = option.getOptionText();
            this.sortOrder = option.getSortOrder();
            this.voteCount = canViewResult ? option.getVoteCount() : null;
        }
    }
}
