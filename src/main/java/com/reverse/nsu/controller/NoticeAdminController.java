package com.reverse.nsu.controller;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.service.EmailService;
import com.reverse.nsu.service.NoticeService;
import com.reverse.nsu.service.RoleCheckService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/notices")
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;
    private final RoleCheckService roleCheckService;
    private final EmailService emailService;

    /**
     * 공지사항 등록 및 수정 (관리자 전용)
     */
    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody NoticeAdminRequestDto dto,
            HttpServletRequest request
    ) {
        try {
            String userId = resolveUserId(request);
            if (userId == null) return unauthorizedResponse();
            if (!roleCheckService.isAdmin(userId)) return forbiddenResponse();

            if (dto.getPostId() != null) {
                NoticeAdminResponseDto response = noticeService.update(dto, userId);
                emailService.sendAuditLog(userId, "공지사항 수정 (ID: " + dto.getPostId() + ") - 제목: " + dto.getTitle());
                return ResponseEntity.ok(ApiResponse.ok(response, "공지사항이 수정되었습니다."));
            } else {
                NoticeAdminResponseDto response = noticeService.create(dto, userId);
                emailService.sendAuditLog(userId, "공지사항 등록 - 제목: " + dto.getTitle());
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
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(
            @PathVariable Integer postId,
            HttpServletRequest request
    ) {
        try {
            String userId = resolveUserId(request);
            if (userId == null) return unauthorizedResponse();
            if (!roleCheckService.isAdmin(userId)) return forbiddenResponse();

            noticeService.delete(postId, userId);
            emailService.sendAuditLog(userId, "공지사항 삭제 (ID: " + postId + ")");
            return ResponseEntity.ok(ApiResponse.ok(null, "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("DELETE_FAILED", e.getMessage()));
        }
    }

    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    private ResponseEntity<?> unauthorizedResponse() {
        return ResponseEntity.status(401).body(ApiResponse.error("UNAUTHORIZED", "로그인이 필요하거나 토큰이 유효하지 않습니다."));
    }

    private ResponseEntity<?> forbiddenResponse() {
        return ResponseEntity.status(403).body(ApiResponse.error("FORBIDDEN", "관리자 권한이 필요합니다."));
    }
}
