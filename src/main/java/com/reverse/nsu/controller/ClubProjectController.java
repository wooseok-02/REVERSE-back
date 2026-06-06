package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ClubProjectRequestDto;
import com.reverse.nsu.entity.ClubProject;
import com.reverse.nsu.service.ClubProjectService;
import com.reverse.nsu.service.EmailService;
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
@RequestMapping("/api/club-project")
@RequiredArgsConstructor
public class ClubProjectController {

    private final ClubProjectService clubProjectService;
    private final RoleCheckService roleCheckService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<ClubProject>> getAll() {
        return ResponseEntity.ok(clubProjectService.getAll());
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        return ResponseEntity.ok(clubProjectService.uploadThumbnailImage(file));
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody ClubProjectRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ClubProject result = clubProjectService.save(dto);
        emailService.sendAuditLog(userId, "프로젝트 소개 등록 - 이름: " + dto.getProjectName());
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
        return ResponseEntity.ok(clubProjectService.uploadThumbnailImage(file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ClubProjectRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ClubProject result = clubProjectService.update(id, dto);
        emailService.sendAuditLog(userId, "프로젝트 소개 수정 (ID: " + id + ") - 이름: " + dto.getProjectName());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        clubProjectService.delete(id);
        emailService.sendAuditLog(userId, "프로젝트 소개 삭제 (ID: " + id + ")");
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
