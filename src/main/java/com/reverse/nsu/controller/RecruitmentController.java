package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApplicationRequestDto; // 지원서 요청 DTO
import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.service.RecruitmentService;
import com.reverse.nsu.service.RecruitmentNotifyService;

import jakarta.validation.Valid; // 유효성 검사 어노테이션
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruit")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    private final RecruitmentNotifyService notifyService;
    /**
     * 1. 공고 전체 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<RecruitmentResponseDto>> getAll() {
        return ResponseEntity.ok(recruitmentService.getAll());
    }

    /**
     * 2. 공고 상세 페이지 조회
     * 상세 모집 소개 및 신청 폼 데이터를 포함합니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecruitmentResponseDto> getRecruitPage(@PathVariable Integer id) {
        return ResponseEntity.ok(recruitmentService.getRecruitPage(id));
    }

    /**
     * 모집 공고 상태 조회
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getApplyStatus(@PathVariable Integer id) {
        // 공고의 시작/종료일과 현재 시간을 비교해서 true/false 반환
        boolean isAvailable = recruitmentService.checkApplyPeriod(id);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    /**
     * 페이지 ID 조회
     */
    @GetMapping("/{id}/gallery")
    public ResponseEntity<List<RecruitmentResponseDto.GalleryDetails>> getGalleries(
            @PathVariable Integer id,
            @RequestParam(required = false) String tag) {

        // 페이지 ID 조회 로직 (생략 가능, 기존 getRecruitPage 로직 활용)
        Integer pageId = recruitmentService.getPageIdByRecruitmentId(id);

        return ResponseEntity.ok(recruitmentService.getGalleriesByTag(pageId, tag));
    }

    /**
     * 알림 구독 등록
     * POST /api/recruit/subscribe
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        notifyService.subscribe(email);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", email + " 주소로 알림 구독이 완료되었습니다."
        ));
    }
}