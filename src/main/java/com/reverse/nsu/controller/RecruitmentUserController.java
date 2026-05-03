package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApplicationRequestDto; // [수정] 변경된 DTO 임포트
import com.reverse.nsu.service.RecruitmentApplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recruit/user")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentUserController {

    private final RecruitmentApplyService recruitmentApplyService;

    /**
     * 지원자가 지원서를 제출하는 API
     * [수정] RecruitmentRequestDto -> ApplicationRequestDto로 변경
     */
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@Valid @RequestBody ApplicationRequestDto dto) {
        try {
            // 서비스 레이어의 제출 메서드 호출
            recruitmentApplyService.submitApplication(dto);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "지원서가 성공적으로 접수되었습니다. 화이팅!"
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            // 중복 신청, 기간 외 신청, 개인정보 미동의 등 비즈니스 예외 처리
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            // 기타 서버 에러 처리
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "접수 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}