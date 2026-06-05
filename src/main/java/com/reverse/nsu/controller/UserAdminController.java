package com.reverse.nsu.controller;

import com.reverse.nsu.service.EmailService;
import com.reverse.nsu.service.RoleCheckService;
import com.reverse.nsu.service.UserAdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 최고관리자 전용 회원 관리 API
 * Base Path: /api/admin/users
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;
    private final RoleCheckService roleCheckService;
    private final EmailService emailService;

    /**
     * 회원 권한 수정
     * - 최고관리자(roleId=1)만 가능
     */
    @PatchMapping("/{targetUserId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable String targetUserId,
            @RequestBody Map<String, Integer> body,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (userId == null) return unauthorized();
        if (!roleCheckService.isSuperAdmin(userId)) return forbidden();

        Integer newRoleId = body.get("roleId");
        if (newRoleId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "roleId는 필수입니다."));
        }

        try {
            userAdminService.updateUserRole(targetUserId, newRoleId);
            emailService.sendAuditLog(userId, "회원 권한 수정 - 대상: " + targetUserId + ", 변경된 roleId: " + newRoleId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "권한이 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 회원 강제 탈퇴
     * - 최고관리자(roleId=1)만 가능
     */
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<?> forceWithdraw(
            @PathVariable String targetUserId,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (userId == null) return unauthorized();
        if (!roleCheckService.isSuperAdmin(userId)) return forbidden();

        try {
            userAdminService.forceWithdraw(targetUserId);
            emailService.sendAuditLog(userId, "회원 강제 탈퇴 - 대상: " + targetUserId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "강제 탈퇴 처리되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요한 서비스입니다."));
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of("message", "최고관리자만 접근할 수 있습니다."));
    }
}
