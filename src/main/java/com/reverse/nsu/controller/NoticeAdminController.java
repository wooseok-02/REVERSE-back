package com.reverse.nsu.controller;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/notices")
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;

    /**
     * [핵심 수정] 가짜 신분증(X-User-Id) 로직을 완전히 제거하고,
     * 인터셉터(JwtInterceptor)가 HttpServletRequest에 넣어준 유저 ID만 사용합니다.
     */
    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    /**
     * 공지사항 등록 및 수정 (관리자 전용)
     */
    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody NoticeAdminRequestDto dto,
            HttpServletRequest request
    ) {
        try {
            // 이제 debugUserId는 받지도, 쓰지도 않습니다.
            String userId = resolveUserId(request);
            if (userId == null || userId.trim().isEmpty()) return unauthorizedResponse();

            if (dto.getNoticeId() != null) {
                NoticeAdminResponseDto response = noticeService.update(dto, userId);
                return ResponseEntity.ok(ApiResponse.ok(response, "공지사항이 수정되었습니다."));
            } else {
                NoticeAdminResponseDto response = noticeService.create(dto, userId);
                return ResponseEntity.status(201)
                        .body(ApiResponse.ok(response, "공지사항이 등록되었습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("BAD_REQUEST", e.getMessage()));
        }
    }

    /**
     * 공지사항 삭제 (관리자 전용)
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> delete(
            @PathVariable Integer noticeId,
            HttpServletRequest request
    ) {
        try {
            String userId = resolveUserId(request);
            if (userId == null || userId.trim().isEmpty()) return unauthorizedResponse();

            noticeService.delete(noticeId, userId);
            return ResponseEntity.ok(ApiResponse.ok(null, "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("DELETE_FAILED", e.getMessage()));
        }
    }

    private ResponseEntity<?> unauthorizedResponse() {
        return ResponseEntity.status(401).body(ApiResponse.error("UNAUTHORIZED", "로그인이 필요하거나 토큰이 유효하지 않습니다."));
    }
}