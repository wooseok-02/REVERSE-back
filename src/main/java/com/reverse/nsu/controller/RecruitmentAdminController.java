package com.reverse.nsu.controller;

import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.dto.RecruitmentStatusRequestDto;
import com.reverse.nsu.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 이 부분이 중요!
import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestController
@RequestMapping("/api/recruit/admin")
@RequiredArgsConstructor
public class RecruitmentAdminController {

    private final RecruitmentService recruitmentService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestParam Integer roleId,
            @RequestBody RecruitmentStatusRequestDto dto
    ) {
        // 서비스 호출
        RecruitmentResponseDto result = recruitmentService.updateStatus(id, roleId, dto);

        // 필요한 데이터만 골라서 Map으로 응답 구성
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "모집 상태가 변경되었습니다.",
                "data", Map.of(
                        "id", result.getId(),
                        "isActive", result.getIsActive()
                )
        ));
    }
}