package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApplicationRequestDto;
import com.reverse.nsu.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/recruit/apply")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentApplyController {

    private final RecruitmentService recruitmentService;

    // 지원서 제출 (userName, userMajor 등 필드 사용)
    @PostMapping
    public ResponseEntity<?> submit(@RequestBody ApplicationRequestDto dto) {
        recruitmentService.submitApplication(dto);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "지원서가 정상적으로 접수되었습니다. 행운을 빕니다!"
        ));
    }
}