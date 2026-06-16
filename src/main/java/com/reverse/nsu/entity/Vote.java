package com.reverse.nsu.entity;

import com.reverse.nsu.dto.VoteRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VOTE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteId;

    @Column(name = "userId", nullable = false, length = 15)
    private String userId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "isMultiple", nullable = false)
    private Boolean isMultiple = false;

    /**
     * 비밀투표 여부.
     * true  = 비밀투표: 생성자·관리자만 결과(voteCount) 조회 가능
     * false = 공개투표: resultViewRole 기준으로 결과 조회 가능
     */
    @Column(name = "isSecret", nullable = false)
    private Boolean isSecret = false;

    /**
     * 투표 참가 가능 최소 roleId. roleId 가 이 값 이하인 사용자만 투표 가능.
     * 1=최고관리자, 2=관리자, 3=정회원, 4=준회원, 5=게스트
     * default: 3 (정회원 이상)
     */
    @Column(name = "participantRole", nullable = false)
    private Integer participantRole = 3;

    /**
     * 공개투표(isSecret=false)일 때 결과를 조회할 수 있는 최소 roleId.
     * roleId 가 이 값 이하인 사용자에게 결과(voteCount) 공개.
     * 비밀투표일 때는 이 값을 무시하고 생성자·관리자만 허용.
     * default: 3 (정회원 이상)
     */
    @Column(name = "resultViewRole", nullable = false)
    private Integer resultViewRole = 3;

    @Column(name = "createdDate", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<VoteOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteRecord> records = new ArrayList<>();

    public static Vote create(VoteRequestDto dto, String userId) {
        Vote vote = new Vote();
        vote.userId = userId;
        vote.title = dto.getTitle();
        vote.content = dto.getContent();
        vote.deadline = dto.getDeadline();
        vote.isMultiple = dto.getIsMultiple() != null ? dto.getIsMultiple() : false;
        vote.isSecret = dto.getIsSecret() != null ? dto.getIsSecret() : false;
        vote.participantRole = dto.getParticipantRole() != null ? dto.getParticipantRole() : 3;
        vote.resultViewRole = dto.getResultViewRole() != null ? dto.getResultViewRole() : 3;
        vote.createdDate = LocalDateTime.now();
        return vote;
    }

    public void update(VoteRequestDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.deadline = dto.getDeadline();
        if (dto.getIsMultiple() != null) this.isMultiple = dto.getIsMultiple();
        if (dto.getIsSecret() != null) this.isSecret = dto.getIsSecret();
        if (dto.getParticipantRole() != null) this.participantRole = dto.getParticipantRole();
        if (dto.getResultViewRole() != null) this.resultViewRole = dto.getResultViewRole();
        this.modifiedDate = LocalDateTime.now();
    }

    public boolean isClosed() {
        return deadline != null && LocalDateTime.now().isAfter(deadline);
    }
}
