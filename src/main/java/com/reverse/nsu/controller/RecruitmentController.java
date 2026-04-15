package com.reverse.nsu.controller;

import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.dto.RecruitmentRequestDto;
import com.reverse.nsu.entity.Recruitment;
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

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<RecruitmentResponseDto>> getAll() {
        return ResponseEntity.ok(recruitmentService.getAll());
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<RecruitmentResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(recruitmentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RecruitmentResponseDto> save(@RequestBody RecruitmentRequestDto dto) {
        return ResponseEntity.ok(recruitmentService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecruitmentResponseDto> update(@PathVariable Integer id, @RequestBody RecruitmentRequestDto dto) {
        return ResponseEntity.ok(recruitmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        recruitmentService.delete(id);
        return ResponseEntity.ok("성공적으로 삭제되었습니다. ID: " + id);
    }
}