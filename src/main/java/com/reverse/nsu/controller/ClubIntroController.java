package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ClubIntroRequestDto;
import com.reverse.nsu.dto.ClubIntroResponseDto;
import com.reverse.nsu.entity.ClubIntro;
import com.reverse.nsu.service.ClubIntroService;
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
@RequestMapping("/api/club-intro")
@RequiredArgsConstructor
public class ClubIntroController {

    private final ClubIntroService clubIntroService;
    private final RoleCheckService roleCheckService;
    private final EmailService emailService;

    @GetMapping("/main")
    public ResponseEntity<List<ClubIntroResponseDto>> getMainIntro() {
        return ResponseEntity.ok(clubIntroService.getActiveClubIntros());
    }

    @GetMapping
    public ResponseEntity<List<ClubIntro>> getAll() {
        return ResponseEntity.ok(clubIntroService.getAll());
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        return ResponseEntity.ok(clubIntroService.uploadBannerImage(file));
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody ClubIntroRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ClubIntro result = clubIntroService.save(dto);
        emailService.sendAuditLog(userId, "동아리 소개 등록 - 제목: " + dto.getTitle());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<?> updateImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        return ResponseEntity.ok(clubIntroService.uploadBannerImage(file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody ClubIntroRequestDto dto,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        ClubIntro result = clubIntroService.update(id, dto);
        emailService.sendAuditLog(userId, "동아리 소개 수정 (ID: " + id + ") - 제목: " + dto.getTitle());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        clubIntroService.delete(id);
        emailService.sendAuditLog(userId, "동아리 소개 삭제 (ID: " + id + ")");
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
