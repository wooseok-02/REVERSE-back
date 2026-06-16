package com.reverse.nsu.service;

import com.reverse.nsu.dto.VoteListResponseDto;
import com.reverse.nsu.dto.VoteRequestDto;
import com.reverse.nsu.dto.VoteResponseDto;
import com.reverse.nsu.dto.VoteResultResponseDto;
import com.reverse.nsu.entity.Vote;
import com.reverse.nsu.entity.VoteOption;
import com.reverse.nsu.entity.VoteRecord;
import com.reverse.nsu.repository.UsersRepository;
import com.reverse.nsu.repository.VoteOptionRepository;
import com.reverse.nsu.repository.VoteRecordRepository;
import com.reverse.nsu.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UsersRepository usersRepository;
    private final RoleCheckService roleCheckService;

    // ─────────────────────────────────────────────────────────
    // 투표 생성
    // ─────────────────────────────────────────────────────────

    @Transactional
    public Integer createVote(VoteRequestDto dto, String userId) {
        validateRequest(dto);

        Vote vote = Vote.create(dto, userId);
        voteRepository.save(vote);

        List<String> optionTexts = dto.getOptions();
        for (int i = 0; i < optionTexts.size(); i++) {
            VoteOption option = VoteOption.create(vote, optionTexts.get(i), i);
            voteOptionRepository.save(option);
        }

        return vote.getVoteId();
    }

    // ─────────────────────────────────────────────────────────
    // 투표 목록 조회
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<VoteListResponseDto> getVoteList(Pageable pageable, String userId) {
        return voteRepository.findAllByOrderByCreatedDateDesc(pageable)
                .map(vote -> new VoteListResponseDto(vote, canViewResult(vote, userId)));
    }

    // ─────────────────────────────────────────────────────────
    // 투표 단건 조회 (결과 가시성 포함)
    // ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public VoteResponseDto getVote(Integer voteId, String userId) {
        Vote vote = findVoteOrThrow(voteId);

        Integer myVotedOptionId = null;
        if (userId != null) {
            myVotedOptionId = voteRecordRepository.findByVoteAndUserId(vote, userId)
                    .map(r -> r.getOption().getOptionId())
                    .orElse(null);
        }

        boolean canViewResult = canViewResult(vote, userId);
        return new VoteResponseDto(vote, myVotedOptionId, canViewResult);
    }

    // ─────────────────────────────────────────────────────────
    // 투표 상세 결과 조회
    // ─────────────────────────────────────────────────────────

    /**
     * 투표 결과 상세 조회 (옵션별 voteCount + 투표자 목록).
     *
     * <ul>
     *   <li>비밀투표: 생성자 또는 관리자만 접근 가능. 관리자는 투표자 목록도 공개.</li>
     *   <li>공개투표: 생성자·관리자는 항상 접근. 그 외 resultViewRole 이하 roleId 사용자 접근 가능.
     *       투표자 목록은 관리자만.</li>
     * </ul>
     *
     * @throws SecurityException 접근 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    public VoteResultResponseDto getVoteResult(Integer voteId, String userId) {
        Vote vote = findVoteOrThrow(voteId);
        boolean isAdmin = roleCheckService.isAdmin(userId);
        boolean isCreator = userId != null && userId.equals(vote.getUserId());

        if (vote.getIsSecret()) {
            // 비밀투표: 생성자 또는 관리자만
            if (!isAdmin && !isCreator) {
                throw new SecurityException("비밀투표의 결과는 투표 생성자와 관리자만 조회할 수 있습니다.");
            }
        } else {
            // 공개투표: 생성자·관리자 또는 resultViewRole 이하 사용자
            if (!isAdmin && !isCreator && !canViewResult(vote, userId)) {
                throw new SecurityException("결과를 조회할 권한이 없습니다.");
            }
        }

        // 투표자 목록은 관리자만 공개
        return new VoteResultResponseDto(vote, isAdmin);
    }

    // ─────────────────────────────────────────────────────────
    // 투표하기
    // ─────────────────────────────────────────────────────────

    @Transactional
    public void castVote(Integer voteId, Integer optionId, String userId) {
        Vote vote = findVoteOrThrow(voteId);

        // 참가 권한 체크 — DB 조회 1회로 isAdmin 판단과 roleId 비교를 동시에 처리
        Integer userRoleId = getUserRoleId(userId);
        boolean isAdmin = userRoleId != null && userRoleId <= 2;
        if (!isAdmin && (userRoleId == null || userRoleId > vote.getParticipantRole())) {
            throw new SecurityException("이 투표에 참가할 권한이 없습니다.");
        }

        if (vote.isClosed()) {
            throw new IllegalStateException("마감된 투표입니다.");
        }

        VoteOption option = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
        if (!option.getVote().getVoteId().equals(voteId)) {
            throw new IllegalArgumentException("해당 투표의 항목이 아닙니다.");
        }

        if (vote.getIsMultiple()) {
            // 다중선택: 같은 항목 중복 투표만 방지
            if (voteRecordRepository.existsByVoteAndUserIdAndOption(vote, userId, option)) {
                throw new IllegalStateException("이미 선택한 항목입니다.");
            }
        } else {
            // 단일선택: 한 번이라도 투표했으면 불가
            if (voteRecordRepository.existsByVoteAndUserId(vote, userId)) {
                throw new IllegalStateException("이미 투표하셨습니다.");
            }
        }

        VoteRecord record = VoteRecord.create(vote, option, userId);
        voteRecordRepository.save(record);
        option.incrementCount();
    }

    // ─────────────────────────────────────────────────────────
    // 투표 취소
    // ─────────────────────────────────────────────────────────

    @Transactional
    public void cancelVote(Integer voteId, String userId, Integer optionId) {
        Vote vote = findVoteOrThrow(voteId);

        if (vote.isClosed()) {
            throw new IllegalStateException("마감된 투표는 취소할 수 없습니다.");
        }

        VoteRecord record;
        if (vote.getIsMultiple() && optionId != null) {
            // 다중선택: 특정 항목 취소
            VoteOption option = voteOptionRepository.findById(optionId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
            record = voteRecordRepository.findByVoteAndUserIdAndOption(vote, userId, option)
                    .orElseThrow(() -> new IllegalStateException("해당 항목에 투표한 기록이 없습니다."));
        } else {
            // 단일선택: 유일한 투표 취소
            record = voteRecordRepository.findByVoteAndUserId(vote, userId)
                    .orElseThrow(() -> new IllegalStateException("투표 기록이 없습니다."));
        }

        record.getOption().decrementCount();
        voteRecordRepository.delete(record);
    }

    // ─────────────────────────────────────────────────────────
    // 투표 수정 (투표 시작 전·생성자 또는 관리자)
    // ─────────────────────────────────────────────────────────

    @Transactional
    public void updateVote(Integer voteId, VoteRequestDto dto, String userId) {
        Vote vote = findVoteOrThrow(voteId);
        boolean isAdmin = roleCheckService.isAdmin(userId);

        if (!vote.getUserId().equals(userId) && !isAdmin) {
            throw new SecurityException("수정 권한이 없습니다.");
        }
        if (!vote.getRecords().isEmpty()) {
            throw new IllegalStateException("투표가 시작된 후에는 수정할 수 없습니다.");
        }

        validateRequest(dto);
        vote.update(dto);

        voteOptionRepository.deleteAllByVote(vote);

        List<String> optionTexts = dto.getOptions();
        for (int i = 0; i < optionTexts.size(); i++) {
            VoteOption option = VoteOption.create(vote, optionTexts.get(i), i);
            voteOptionRepository.save(option);
        }
    }

    // ─────────────────────────────────────────────────────────
    // 투표 삭제 (생성자 또는 관리자)
    // ─────────────────────────────────────────────────────────

    @Transactional
    public void deleteVote(Integer voteId, String userId) {
        Vote vote = findVoteOrThrow(voteId);

        if (!vote.getUserId().equals(userId) && !roleCheckService.isAdmin(userId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        voteRepository.delete(vote);
    }

    // ─────────────────────────────────────────────────────────
    // 내부 헬퍼
    // ─────────────────────────────────────────────────────────

    private Vote findVoteOrThrow(Integer voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다. ID: " + voteId));
    }

    /**
     * 해당 사용자가 투표 결과(voteCount)를 조회할 수 있는지 판단.
     * - 관리자 또는 생성자: 항상 true
     * - 비밀투표: 위 두 경우만 true
     * - 공개투표: userId의 roleId가 vote.resultViewRole 이하이면 true
     */
    private boolean canViewResult(Vote vote, String userId) {
        if (userId == null) return false;
        if (roleCheckService.isAdmin(userId)) return true;
        if (userId.equals(vote.getUserId())) return true;
        if (vote.getIsSecret()) return false;

        Integer userRoleId = getUserRoleId(userId);
        return userRoleId != null && userRoleId <= vote.getResultViewRole();
    }

    /**
     * DB에서 사용자의 roleId를 조회한다.
     */
    private Integer getUserRoleId(String userId) {
        if (userId == null) return null;
        return usersRepository.findById(userId)
                .map(u -> u.getRole() != null ? u.getRole().getRoleId() : null)
                .orElse(null);
    }

    private void validateRequest(VoteRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (dto.getOptions() == null || dto.getOptions().size() < 2) {
            throw new IllegalArgumentException("투표 항목은 최소 2개 이상 필요합니다.");
        }
    }
}
