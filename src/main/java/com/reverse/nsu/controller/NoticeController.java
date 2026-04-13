package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApiResponse;
import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.dto.NoticeAdminResponseDto;
import com.reverse.nsu.dto.NoticeListResponseDto;
import com.reverse.nsu.dto.NoticeResponseDto;
import com.reverse.nsu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 단건 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> getOne(@PathVariable Integer noticeId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(noticeService.getOne(noticeId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "해당 공지사항을 찾을 수 없습니다."));
        }
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeListResponseDto>>> getAll() {
        try {
            return ResponseEntity.ok(ApiResponse.ok(noticeService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("SERVER_ERROR", "서버 내부 오류"));
        }
    }

    // 등록/수정
@PostMapping("/posts/notices")
public ResponseEntity<ApiResponse<NoticeAdminResponseDto>> save(
        @RequestHeader("Authorization") String token,
        @RequestBody NoticeAdminRequestDto dto
) {
    try {
        // noticeId 있으면 수정, 없으면 등록
        // TODO: token에서 userId 추출 (JWT 붙일 때 구현)
        String userId = "admin"; // 임시
        if (dto.getNoticeId() != null) {
            return ResponseEntity.ok(ApiResponse.ok(noticeService.update(dto, userId), "공지사항이 수정되었습니다."));
        }
        return ResponseEntity.status(201)
                .body(ApiResponse.ok(noticeService.create(dto, userId), "공지사항이 등록되었습니다."));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(400)
                .body(ApiResponse.error("MISSING_FIELD", "필수 값이 누락되었습니다."));
    }
}

    // 삭제
    @DeleteMapping("/posts/notices/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeAdminResponseDto>> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer noticeId
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    new NoticeAdminResponseDto(noticeService.delete(noticeId)), "공지사항이 삭제되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "존재하지 않는 공지사항입니다."));
        }
    }
}