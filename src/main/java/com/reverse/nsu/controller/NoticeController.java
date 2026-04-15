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

    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponseDto>> getOne(@PathVariable Integer noticeId) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(noticeService.getOne(noticeId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "해당 공지사항을 찾을 수 없습니다."));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeListResponseDto>>> getAll() {
        try {
            return ResponseEntity.ok(ApiResponse.ok(noticeService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("SERVER_ERROR", "서버 내부 오류"));
        }
    }
}