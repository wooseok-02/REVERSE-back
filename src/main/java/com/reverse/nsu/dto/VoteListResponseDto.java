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
    private final Boolean isSecret;
    private final Integer participantRole;
    private final Integer resultViewRole;
    private final int optionCount;
    /** 결과 조회 권한이 없으면 null (비밀투표 또는 resultViewRole 미충족) */
    private final Integer totalVoteCount;
    private final LocalDateTime createdDate;

    public VoteListResponseDto(Vote vote, boolean canViewCount) {
        this.voteId = vote.getVoteId();
        this.userId = vote.getUserId();
        this.title = vote.getTitle();
        this.deadline = vote.getDeadline();
        this.isClosed = vote.isClosed();
        this.isSecret = vote.getIsSecret();
        this.participantRole = vote.getParticipantRole();
        this.resultViewRole = vote.getResultViewRole();
        this.optionCount = vote.getOptions().size();
        this.totalVoteCount = canViewCount
                ? vote.getOptions().stream().mapToInt(o -> o.getVoteCount()).sum()
                : null;
        this.createdDate = vote.getCreatedDate();
    }
}
