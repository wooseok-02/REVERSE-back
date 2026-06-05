package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Vote;
import com.reverse.nsu.entity.VoteOption;
import com.reverse.nsu.entity.VoteRecord;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 투표 상세 결과 DTO.
 * - 관리자·최고관리자: 옵션별 voteCount + 투표자 userId 목록 모두 공개
 * - 생성자(비밀투표): voteCount만 공개, 투표자 목록은 숨김
 * - 권한 있는 일반 유저(공개투표): voteCount만 공개, 투표자 목록은 숨김
 */
@Getter
public class VoteResultResponseDto {

    private final Integer voteId;
    private final String creatorId;
    private final String title;
    private final String content;
    private final Boolean isSecret;
    private final Integer participantRole;
    private final Integer resultViewRole;
    private final LocalDateTime deadline;
    private final Boolean isClosed;
    private final int totalVoteCount;
    private final List<OptionResultDto> options;

    public VoteResultResponseDto(Vote vote, boolean includeVoters) {
        this.voteId = vote.getVoteId();
        this.creatorId = vote.getUserId();
        this.title = vote.getTitle();
        this.content = vote.getContent();
        this.isSecret = vote.getIsSecret();
        this.participantRole = vote.getParticipantRole();
        this.resultViewRole = vote.getResultViewRole();
        this.deadline = vote.getDeadline();
        this.isClosed = vote.isClosed();

        // 옵션별 투표 기록 그룹핑
        Map<Integer, List<String>> votersByOption = vote.getRecords().stream()
                .collect(Collectors.groupingBy(
                        r -> r.getOption().getOptionId(),
                        Collectors.mapping(VoteRecord::getUserId, Collectors.toList())
                ));

        this.options = vote.getOptions().stream()
                .map(opt -> new OptionResultDto(opt, votersByOption, includeVoters))
                .collect(Collectors.toList());

        this.totalVoteCount = this.options.stream()
                .mapToInt(o -> o.voteCount)
                .sum();
    }

    @Getter
    public static class OptionResultDto {
        private final Integer optionId;
        private final String optionText;
        private final Integer sortOrder;
        private final int voteCount;
        /**
         * 투표자 userId 목록.
         * 관리자만 공개됨 — 그 외에는 null.
         */
        private final List<String> voters;

        public OptionResultDto(VoteOption option, Map<Integer, List<String>> votersByOption, boolean includeVoters) {
            this.optionId = option.getOptionId();
            this.optionText = option.getOptionText();
            this.sortOrder = option.getSortOrder();
            this.voteCount = option.getVoteCount();
            this.voters = includeVoters
                    ? votersByOption.getOrDefault(option.getOptionId(), List.of())
                    : null;
        }
    }
}
