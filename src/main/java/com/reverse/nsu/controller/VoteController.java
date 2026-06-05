package com.reverse.nsu.controller;

import com.reverse.nsu.dto.VoteListResponseDto;
import com.reverse.nsu.dto.VoteRequestDto;
import com.reverse.nsu.dto.VoteResponseDto;
import com.reverse.nsu.dto.VoteResultResponseDto;
import com.reverse.nsu.service.VoteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    /**
     * 투표 생성
     * - 인증 필수
     */
    @PostMapping
    public ResponseEntity<?> createVote(
            @RequestBody VoteRequestDto dto,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        Integer voteId = voteService.createVote(dto, userId);
        return ResponseEntity.ok(Map.of("status", "success", "voteId", voteId, "message", "투표가 등록되었습니다."));
    }

    /**
     * 투표 목록 조회 (10개씩 페이징)
     * - 인증 선택 (로그인 시 결과 집계 가시성 반영)
     */
    @GetMapping
    public ResponseEntity<?> getVoteList(
            @PageableDefault(size = 10) Pageable pageable,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        Page<VoteListResponseDto> result = voteService.getVoteList(pageable, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 투표 단건 조회
     * - 인증 선택 (로그인 시 내 투표 여부 및 결과 가시성 반영)
     * - 비밀투표이고 결과 조회 권한이 없으면 voteCount는 null로 반환
     */
    @GetMapping("/{voteId}")
    public ResponseEntity<?> getVote(
            @PathVariable Integer voteId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        VoteResponseDto result = voteService.getVote(voteId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 투표 상세 결과 조회
     * - 비밀투표: 생성자 또는 관리자만 접근 가능
     * - 공개투표: resultViewRole 이하 roleId 사용자 접근 가능 (관리자/생성자 항상 가능)
     * - 투표자 userId 목록은 관리자만 포함됨
     */
    @GetMapping("/{voteId}/result")
    public ResponseEntity<?> getVoteResult(
            @PathVariable Integer voteId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        try {
            VoteResultResponseDto result = voteService.getVoteResult(voteId, userId);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 투표하기
     * - 인증 필수
     * - vote.participantRole 이하 roleId 사용자만 가능 (관리자 항상 가능)
     */
    @PostMapping("/{voteId}/vote")
    public ResponseEntity<?> castVote(
            @PathVariable Integer voteId,
            @RequestBody Map<String, Integer> body,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        Integer optionId = body.get("optionId");
        if (optionId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "optionId는 필수입니다."));
        }

        try {
            voteService.castVote(voteId, optionId, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "투표가 완료되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 투표 취소
     * - 인증 필수, 마감된 투표는 취소 불가
     * - 다중선택(isMultiple=true) 시 optionId 쿼리 파라미터로 취소할 항목 지정
     */
    @DeleteMapping("/{voteId}/vote")
    public ResponseEntity<?> cancelVote(
            @PathVariable Integer voteId,
            @RequestParam(required = false) Integer optionId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        try {
            voteService.cancelVote(voteId, userId, optionId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "투표가 취소되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 투표 수정
     * - 인증 필수, 생성자 또는 관리자
     * - 투표 참여자가 있으면 수정 불가
     */
    @PatchMapping("/{voteId}")
    public ResponseEntity<?> updateVote(
            @PathVariable Integer voteId,
            @RequestBody VoteRequestDto dto,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        try {
            voteService.updateVote(voteId, dto, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "수정되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 투표 삭제
     * - 인증 필수, 생성자 또는 관리자
     */
    @DeleteMapping("/{voteId}")
    public ResponseEntity<?> deleteVote(
            @PathVariable Integer voteId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        try {
            voteService.deleteVote(voteId, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "삭제되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요한 서비스입니다."));
    }
}
