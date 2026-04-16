package com.reverse.nsu.controller;

import com.reverse.nsu.dto.RecruitmentRequestDto;
import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/recruit/admin")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentAdminController {

    private final RecruitmentService recruitmentService;

    // 1. 공고 생성 및 메일 알림
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RecruitmentRequestDto dto) {
        recruitmentService.save(dto, "1");
        return ResponseEntity.ok(Map.of("status", "success", "message", "공고 등록 및 알림 발송 완료"));
    }

    // 2. 공고 상세 페이지 내용 저장/수정
    @PostMapping("/page/{id}")
    public ResponseEntity<?> savePage(@PathVariable Integer id, @RequestBody RecruitmentResponseDto.PageDetails dto) {
        recruitmentService.createOrUpdatePage(id, dto, "1");
        return ResponseEntity.ok(Map.of("status", "success", "message", "상세 페이지 저장 완료"));
    }

    // 3. 공고 및 모든 관련 데이터 삭제 (우리가 해결한 그 메서드!)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        recruitmentService.delete(id);
        return ResponseEntity.ok("공고 및 관련 데이터가 완전히 삭제되었습니다. ID: " + id);
    }
}