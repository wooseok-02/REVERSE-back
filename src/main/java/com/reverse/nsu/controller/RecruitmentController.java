package com.reverse.nsu.controller;

import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recruit")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    // 공고 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<RecruitmentResponseDto>> getAll() {
        return ResponseEntity.ok(recruitmentService.getAll());
    }

    // 공고 상세 페이지 조회 (상세 레이아웃 포함)
    @GetMapping("/{id}")
    public ResponseEntity<RecruitmentResponseDto> getRecruitPage(@PathVariable Integer id) {
        return ResponseEntity.ok(recruitmentService.getRecruitPage(id));
    }
}