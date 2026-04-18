package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ScheduleCategoryResponseDto;
import com.reverse.nsu.dto.ScheduleResponseDto;
import com.reverse.nsu.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 카테고리 목록 조회 (노출 중인 것만)
    @GetMapping("/category")
    public ResponseEntity<List<ScheduleCategoryResponseDto>> getCategories() {
        return ResponseEntity.ok(scheduleService.getVisibleCategories());
    }

    // 월별 일정 조회 (공개된 것만)
    // GET /api/schedule?year=2026&month=4  (파라미터 생략 시 현재 연월)
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getSchedules(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();
        int m = (month != null) ? month : now.getMonthValue();
        return ResponseEntity.ok(scheduleService.getSchedulesByMonth(y, m));
    }

    // 일정 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }
}
