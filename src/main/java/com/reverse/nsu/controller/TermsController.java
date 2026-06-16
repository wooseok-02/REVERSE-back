package com.reverse.nsu.controller;

import com.reverse.nsu.entity.Terms;
import com.reverse.nsu.service.EmailService;
import com.reverse.nsu.service.RoleCheckService;
import com.reverse.nsu.service.TermsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class TermsController {

    private final TermsService termsService;
    private final RoleCheckService roleCheckService;
    private final EmailService emailService;

    @GetMapping("/current")
    public ResponseEntity<Terms> getCurrentTerms() {
        return ResponseEntity.ok(termsService.getCurrentTerms());
    }

    @GetMapping
    public List<Terms> getAllTerms() {
        return termsService.getAllTerms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Terms> getTermsById(@PathVariable Integer id) {
        return ResponseEntity.ok(termsService.getTermsById(id));
    }

    @PostMapping
    public ResponseEntity<?> createTerms(
            @RequestBody Terms terms,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        Terms result = termsService.saveTerms(terms);
        emailService.sendAuditLog(userId, "약관 등록 - 버전: " + terms.getVersion());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTerms(
            @PathVariable Integer id,
            @RequestBody Terms termsDetails,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        termsDetails.setTermsId(id);
        Terms result = termsService.saveTerms(termsDetails);
        emailService.sendAuditLog(userId, "약관 수정 (ID: " + id + ") - 버전: " + termsDetails.getVersion());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTerms(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorized();
        if (!roleCheckService.isAdmin(userId)) return forbidden();
        termsService.deleteTerms(id);
        emailService.sendAuditLog(userId, "약관 삭제 (ID: " + id + ")");
        return ResponseEntity.ok(Map.of("message", id + "번 약관이 성공적으로 삭제되었습니다."));
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
