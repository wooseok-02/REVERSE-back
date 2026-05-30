package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ProjectResponseDto;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.UsersRepository;
import com.reverse.nsu.service.ProjectAdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class ProjectAdminController {

    private final ProjectAdminService projectAdminService;
    private final UsersRepository usersRepository;

    /**
     * Admin 1. 프로젝트 목록 전체 조회
     */
    @GetMapping
    public ResponseEntity<?> getAllProjects(
            @PageableDefault(size = 10) Pageable pageable,
            HttpServletRequest request) {

        String currentUserId = getCurrentUserIdFromRequest(request);

        // 🛡️ 관리자 권한 체크 (roleId 1, 2 수색)
        if (!isAdmin(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false, "message", "관리자만 접근 가능한 페이지입니다."
            ));
        }

        Page<ProjectResponseDto> projects = projectAdminService.getAllProjectsForAdmin(pageable);
        return ResponseEntity.ok(projects);
    }

    /**
     * Admin 2. 프로젝트 강제 종료 처리
     */
    @PatchMapping("/{projectId}/close")
    public ResponseEntity<Map<String, Object>> closeProjectForce(
            @PathVariable("projectId") Integer projectId,
            HttpServletRequest request) {

        String currentUserId = getCurrentUserIdFromRequest(request);
        Map<String, Object> result = new HashMap<>();

        // 🛡️ 관리자 권한 체크 (roleId 1, 2 수색)
        if (!isAdmin(currentUserId)) {
            result.put("success", false);
            result.put("message", "해당 작업을 수행할 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }

        try {
            projectAdminService.closeProjectForce(projectId);
            return ResponseEntity.ok(result); // 성공 시 빈 JSON ({})
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Admin 3. 프로젝트 강제 삭제 처리
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> deleteProjectForce(
            @PathVariable("projectId") Integer projectId,
            HttpServletRequest request) {

        String currentUserId = getCurrentUserIdFromRequest(request);
        Map<String, Object> result = new HashMap<>();

        // 🛡️ 관리자 권한 체크 (roleId 1, 2 수색)
        if (!isAdmin(currentUserId)) {
            result.put("success", false);
            result.put("message", "해당 작업을 수행할 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }

        try {
            projectAdminService.deleteProjectForce(projectId);
            return ResponseEntity.ok(result); // 성공 시 빈 JSON ({})
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 🔐 [Helper Method] 요청에서 유저 ID 추출
     */
    private String getCurrentUserIdFromRequest(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute("userId");
        if (currentUserId == null) {
            currentUserId = (String) request.getAttribute("currentUserId");
        }
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return currentUserId;
    }

    /**
     * 🔐 [Helper Method] roleId 기반 관리자 판별 (1 또는 2면 True)
     */
    private boolean isAdmin(String userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() == null) {
            return false;
        }

        Integer roleId = user.getRole().getRoleId();
        // 💡 roleId가 1이거나 2일 때만 true를 리턴하여 방어합니다.
        return roleId != null && (roleId == 1 || roleId == 2);
    }
}