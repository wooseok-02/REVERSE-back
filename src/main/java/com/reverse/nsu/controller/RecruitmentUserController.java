package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApplicationRequestDto;
import com.reverse.nsu.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recruit/user")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentUserController {

    private final RecruitmentService recruitmentService;

    // 지원자가 지원서를 제출하는 API
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody ApplicationRequestDto dto) {
        try {
            recruitmentService.submitApplication(dto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "지원서가 성공적으로 접수되었습니다. 화이팅!"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "접수 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}