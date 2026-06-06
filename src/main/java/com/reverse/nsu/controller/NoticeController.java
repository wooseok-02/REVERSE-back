package com.reverse.nsu.controller;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.service.NoticeService;
import com.reverse.nsu.service.R2Service;
import com.reverse.nsu.service.RoleCheckService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final R2Service r2Service;
    private final RoleCheckService roleCheckService;

    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    /**
     * 이미지 업로드 (관리자 전용)
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        String userId = resolveUserId(request);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        if (!roleCheckService.isAdmin(userId))
            return ResponseEntity.status(403).body(Map.of("message", "관리자 권한이 필요합니다."));
        return ResponseEntity.ok(r2Service.upload(file, "notice"));
    }

    /**
     * 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NoticeListResponseDto>>> getAll(
            @RequestParam(defaultValue = "전체") String category,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request
    ) {
        boolean isLoggedIn = resolveUserId(request) != null;
        return ResponseEntity.ok(ApiResponse.ok(noticeService.getAll(category, isLoggedIn, page)));
    }

    /**
     * 단건 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> getOne(
            @PathVariable Integer postId,
            HttpServletRequest request
    ) {
        try {
            boolean isLoggedIn = resolveUserId(request) != null;
            return ResponseEntity.ok(ApiResponse.ok(noticeService.getOne(postId, isLoggedIn)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("FORBIDDEN", "공지사항은 회원가입된 사용자만 접근 가능합니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "해당 공지사항을 찾을 수 없습니다."));
        }
    }
}
