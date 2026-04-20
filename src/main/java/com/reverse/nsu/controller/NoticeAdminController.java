package com.reverse.nsu.controller;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/posts/notices")
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;

    // 등록(201) / 수정(200)
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeAdminResponseDto>> save(
            @RequestHeader("Authorization") String token,
            @RequestBody NoticeAdminRequestDto dto,
            HttpServletRequest request

    ) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (dto.getNoticeId() != null) {
                return ResponseEntity.ok(
                        ApiResponse.ok(noticeService.update(dto, userId), "공지사항이 수정되었습니다."));
            }
            return ResponseEntity.status(201)
                    .body(ApiResponse.ok(noticeService.create(dto, userId), "공지사항이 등록되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400)
                    .body(ApiResponse.error("MISSING_FIELD", "필수 값이 누락되었습니다."));
        }
    }

    // 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeAdminResponseDto>> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer noticeId
    ) {
        try {
            return ResponseEntity.ok(
                    ApiResponse.ok(new NoticeAdminResponseDto(noticeService.delete(noticeId)), "공지사항이 삭제되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "존재하지 않는 공지사항입니다."));
        }
    }
}