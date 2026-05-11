package com.reverse.nsu.controller;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.service.NoticeService; // NoticeService로 변경
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/notices")
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService; // NoticeService 주입

    /**
     * 유저 식별 로직 (권한 체크는 Service에서 수행하므로 단순 ID 추출로 변경)
     */
    private String resolveUserId(HttpServletRequest request, String debugUserId) {
        String userId = (String) request.getAttribute("userId");
        return (userId != null) ? userId : debugUserId;
    }

    /**
     * 공지사항 등록 및 수정 (관리자 전용)
     */
    @PostMapping
    public ResponseEntity<?> save(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            @RequestBody NoticeAdminRequestDto dto,
            HttpServletRequest request
    ) {
        try {
            String userId = resolveUserId(request, debugUserId);
            if (userId == null || userId.trim().isEmpty()) return unauthorizedResponse();

            if (dto.getNoticeId() != null) {
                // NoticeService의 수정 로직 호출
                NoticeAdminResponseDto response = noticeService.update(dto, userId);
                return ResponseEntity.ok(ApiResponse.ok(response, "공지사항이 수정되었습니다."));
            } else {
                // NoticeService의 등록 로직 호출
                NoticeAdminResponseDto response = noticeService.create(dto, userId);
                return ResponseEntity.status(201)
                        .body(ApiResponse.ok(response, "공지사항이 등록되었습니다."));
            }
        } catch (Exception e) {
            // 서비스에서 던진 "수정 권한이 없습니다" 등의 예외 처리
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("BAD_REQUEST", e.getMessage()));
        }
    }

    /**
     * 공지사항 삭제 (관리자 전용)
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> delete(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            @PathVariable Integer noticeId,
            HttpServletRequest request
    ) {
        try {
            String userId = resolveUserId(request, debugUserId);
            if (userId == null || userId.trim().isEmpty()) return unauthorizedResponse();

            // NoticeService의 삭제 로직 호출 (userId 전달)
            noticeService.delete(noticeId, userId);
            return ResponseEntity.ok(ApiResponse.ok(null, "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("DELETE_FAILED", e.getMessage()));
        }
    }

    private ResponseEntity<?> unauthorizedResponse() {
        return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
    }
}