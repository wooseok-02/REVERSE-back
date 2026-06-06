package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ScheduleCategoryRequestDto;
import com.reverse.nsu.dto.ScheduleCategoryResponseDto;
import com.reverse.nsu.dto.ScheduleRequestDto;
import com.reverse.nsu.dto.ScheduleResponseDto;
import com.reverse.nsu.service.EmailService;
import com.reverse.nsu.service.RoleCheckService;
import com.reverse.nsu.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule/admin")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ScheduleAdminController {

    private final ScheduleService scheduleService;
    private final RoleCheckService roleCheckService;
    private final EmailService emailService;

    // ───────────────────────────────────────────
    // 카테고리 CRUD
    // ───────────────────────────────────────────

    @GetMapping("/category")
    public ResponseEntity<List<ScheduleCategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(scheduleService.getAllCategories());
    }

    @PostMapping("/category")
    public ResponseEntity<?> saveCategory(
            @RequestBody ScheduleCategoryRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ScheduleCategoryResponseDto result = scheduleService.saveCategory(dto);
        emailService.sendAuditLog(userId, "일정 카테고리 등록 - 이름: " + dto.getCategoryName());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer id,
            @RequestBody ScheduleCategoryRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ScheduleCategoryResponseDto result = scheduleService.updateCategory(id, dto);
        emailService.sendAuditLog(userId, "일정 카테고리 수정 (ID: " + id + ") - 이름: " + dto.getCategoryName());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        scheduleService.deleteCategory(id);
        emailService.sendAuditLog(userId, "일정 카테고리 삭제 (ID: " + id + ")");
        return ResponseEntity.ok("카테고리가 삭제되었습니다. ID: " + id);
    }

    // ───────────────────────────────────────────
    // 일정 CRUD
    // ───────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getSchedules(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(scheduleService.getAllSchedulesByMonth(year, month));
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody ScheduleRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ScheduleResponseDto result = scheduleService.save(dto);
        emailService.sendAuditLog(userId, "일정 등록 - 제목: " + dto.getTitle());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody ScheduleRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ScheduleResponseDto result = scheduleService.update(id, dto);
        emailService.sendAuditLog(userId, "일정 수정 (ID: " + id + ") - 제목: " + dto.getTitle());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        scheduleService.delete(id);
        emailService.sendAuditLog(userId, "일정 삭제 (ID: " + id + ")");
        return ResponseEntity.ok("일정이 삭제되었습니다. ID: " + id);
    }

    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("message", "관리자 권한이 필요합니다."));
    }
}
