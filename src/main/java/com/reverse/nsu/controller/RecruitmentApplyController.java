package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApplicationRequestDto;
import com.reverse.nsu.service.RecruitmentApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruit")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentApplyController {
    private final RecruitmentApplyService applyService;

    // 지원서 제출 API
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplicationRequestDto dto) {
        applyService.submitApplication(dto);
        return ResponseEntity.ok("지원서가 성공적으로 제출되었습니다.");
    }

    // 이메일 알림 구독 API
    @PostMapping("/notify")
    public ResponseEntity<String> subscribe(@RequestParam String email) {
        applyService.subscribeNotification(email);
        return ResponseEntity.ok("공고 알림 구독이 완료되었습니다.");
    }
}