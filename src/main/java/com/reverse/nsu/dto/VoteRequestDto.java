package com.reverse.nsu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class VoteRequestDto {
    private String title;
    private String content;
    private LocalDateTime deadline;
    private Boolean isMultiple;

    /**
     * 비밀투표 여부 (default: false = 공개투표)
     */
    private Boolean isSecret;

    /**
     * 투표 참가 가능 최소 roleId (default: 3 = 정회원 이상)
     * 1=최고관리자, 2=관리자, 3=정회원, 4=준회원, 5=게스트
     */
    private Integer participantRole;

    /**
     * 공개투표일 때 결과 조회 가능 최소 roleId (default: 3 = 정회원 이상)
     * 비밀투표인 경우 이 값은 무시됨
     */
    private Integer resultViewRole;

    private List<String> options;
}
