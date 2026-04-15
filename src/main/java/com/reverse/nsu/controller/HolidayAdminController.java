package com.reverse.nsu.controller;

import com.reverse.nsu.entity.Holiday;
import com.reverse.nsu.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/holiday")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HolidayAdminController {

    private final HolidayService holidayService;

    // 월별 공휴일 조회
    // GET /api/holiday?year=2026&month=4
    @GetMapping
    public ResponseEntity<List<Holiday>> getHolidays(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(holidayService.getHolidaysByMonth(year, month));
    }

    // 특정 연도 공휴일 수동 동기화 (관리자)
    // POST /api/holiday/admin/sync?year=2026
    @PostMapping("/admin/sync")
    public ResponseEntity<Map<String, String>> sync(@RequestParam int year) {
        holidayService.syncHolidays(year);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", year + "년 공휴일 동기화가 완료되었습니다."
        ));
    }
}
