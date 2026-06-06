package com.reverse.nsu.controller;

import com.reverse.nsu.dto.OfficerRequestDto;
import com.reverse.nsu.entity.Officer;
import com.reverse.nsu.service.EmailService;
import com.reverse.nsu.service.OfficerService;
import com.reverse.nsu.service.RoleCheckService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/officer")
@RequiredArgsConstructor
public class OfficerController {

    private final OfficerService officerService;
    private final RoleCheckService roleCheckService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<Officer>> getAll() {
        return ResponseEntity.ok(officerService.getAll());
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        return ResponseEntity.ok(officerService.uploadPhotoImage(file));
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody OfficerRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        Officer result = officerService.save(dto);
        emailService.sendAuditLog(userId, "임원진 등록 - 이름: " + dto.getName());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<?> updateImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        return ResponseEntity.ok(officerService.uploadPhotoImage(file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody OfficerRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        Officer result = officerService.update(id, dto);
        emailService.sendAuditLog(userId, "임원진 수정 (ID: " + id + ") - 이름: " + dto.getName());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        officerService.delete(id);
        emailService.sendAuditLog(userId, "임원진 삭제 (ID: " + id + ")");
        return ResponseEntity.ok("삭제 완료: " + id);
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
