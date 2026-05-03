package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApplicationRequestDto;
import com.reverse.nsu.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recruit/apply") // 경로를 /apply로 명시
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentApplyController {

    private final RecruitmentService recruitmentService;

    /**
     * 상세 모집 신청 페이지 - 제출 버튼 클릭 시 실행
     * POST /api/recruit/apply
     */
    @PostMapping
    public ResponseEntity<?> submit(@Valid @RequestBody ApplicationRequestDto dto) {
        try {
            // Service에서 중복 체크 및 기간 체크 로직 수행
            recruitmentService.submitApplication(dto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "신청이 완료되었습니다."
            ));
        } catch (IllegalStateException e) {
            // "이미 신청하셨습니다" 또는 "신청 기간이 아닙니다" 에러 처리
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}