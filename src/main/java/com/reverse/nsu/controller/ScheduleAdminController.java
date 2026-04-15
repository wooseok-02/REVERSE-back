package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ScheduleCategoryRequestDto;
import com.reverse.nsu.dto.ScheduleCategoryResponseDto;
import com.reverse.nsu.dto.ScheduleRequestDto;
import com.reverse.nsu.dto.ScheduleResponseDto;
import com.reverse.nsu.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule/admin")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ScheduleAdminController {

    private final ScheduleService scheduleService;

    // ───────────────────────────────────────────
    // 카테고리 CRUD
    // ───────────────────────────────────────────

    @GetMapping("/category")
    public ResponseEntity<List<ScheduleCategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(scheduleService.getAllCategories());
    }

    @PostMapping("/category")
    public ResponseEntity<ScheduleCategoryResponseDto> saveCategory(@RequestBody ScheduleCategoryRequestDto dto) {
        return ResponseEntity.ok(scheduleService.saveCategory(dto));
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<ScheduleCategoryResponseDto> updateCategory(
            @PathVariable Integer id,
            @RequestBody ScheduleCategoryRequestDto dto) {
        return ResponseEntity.ok(scheduleService.updateCategory(id, dto));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        scheduleService.deleteCategory(id);
        return ResponseEntity.ok("카테고리가 삭제되었습니다. ID: " + id);
    }

    // ───────────────────────────────────────────
    // 일정 CRUD (모든 일정 포함)
    // ───────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getSchedules(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(scheduleService.getAllSchedulesByMonth(year, month));
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> save(@RequestBody ScheduleRequestDto dto) {
        return ResponseEntity.ok(scheduleService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> update(
            @PathVariable Integer id,
            @RequestBody ScheduleRequestDto dto) {
        return ResponseEntity.ok(scheduleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        scheduleService.delete(id);
        return ResponseEntity.ok("일정이 삭제되었습니다. ID: " + id);
    }
}
