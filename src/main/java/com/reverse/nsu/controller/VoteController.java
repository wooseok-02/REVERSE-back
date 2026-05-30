package com.reverse.nsu.controller;

import com.reverse.nsu.dto.VoteListResponseDto;
import com.reverse.nsu.dto.VoteRequestDto;
import com.reverse.nsu.dto.VoteResponseDto;
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

    @PostMapping
    public ResponseEntity<?> createVote(
            @RequestBody VoteRequestDto dto,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        Integer voteId = voteService.createVote(dto, userId);
        return ResponseEntity.ok(Map.of("status", "success", "voteId", voteId, "message", "투표가 등록되었습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getVoteList(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<VoteListResponseDto> result = voteService.getVoteList(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{voteId}")
    public ResponseEntity<?> getVote(
            @PathVariable Integer voteId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        VoteResponseDto result = voteService.getVote(voteId, userId);
        return ResponseEntity.ok(result);
    }

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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{voteId}/vote")
    public ResponseEntity<?> cancelVote(
            @PathVariable Integer voteId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        try {
            voteService.cancelVote(voteId, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "투표가 취소되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

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
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{voteId}")
    public ResponseEntity<?> deleteVote(
            @PathVariable Integer voteId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();

        try {
            voteService.deleteVote(voteId, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요한 서비스입니다."));
    }
}
