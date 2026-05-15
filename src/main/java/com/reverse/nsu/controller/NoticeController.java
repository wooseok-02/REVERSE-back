package com.reverse.nsu.controller;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.service.NoticeService;
import com.reverse.nsu.service.R2Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final R2Service r2Service;

    // 인터셉터에서 유저 ID 추출 (로그인 여부 확인용)
    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    /**
     * 이미지 업로드 → URL 반환
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(r2Service.upload(file, "notice"));
    }

    /**
     * 목록 조회 (카테고리 필터링)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NoticeListResponseDto>>> getAll(
            @RequestParam(defaultValue = "전체") String category,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request
    ) {
        // userId가 있으면 로그인 상태로 판단
        boolean isLoggedIn = resolveUserId(request) != null;
        return ResponseEntity.ok(ApiResponse.ok(noticeService.getAll(category, isLoggedIn, page)));
    }

    /**
     * 단건 조회 (상세 보기)
     * [수정] noticeId -> postId로 명칭 통일
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