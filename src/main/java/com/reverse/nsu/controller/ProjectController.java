package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ProjectApplyRequestDto;
import com.reverse.nsu.dto.ProjectRequestDto;
import com.reverse.nsu.dto.ProjectResponseDto;
import com.reverse.nsu.entity.ProjectStatus;
import com.reverse.nsu.repository.UsersRepository;
import com.reverse.nsu.service.ProjectService;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UsersRepository usersRepository;

    /**
     * [R] 프로젝트 게시글 목록 확인 (검색 & 필터링 포함)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProjectList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) ProjectStatus status,
            @RequestAttribute(value = "userId", required = false) String currentUserId,
            @PageableDefault(size = 6) Pageable pageable) {

        Page<ProjectResponseDto> projects = projectService.getProjectList(keyword, status, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("projects", projects);
        response.put("currentUserId", currentUserId);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 프로젝트 상세 정보 조회
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProjectDetail(@PathVariable("projectId") Integer projectId) {
        ProjectResponseDto project = projectService.getProjectDetail(projectId);
        return ResponseEntity.ok(project);
    }

    /**
     * [C] 프로젝트 게시글 작성
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProject(
            @RequestBody ProjectRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "프로젝트 게시글 작성 권한이 없습니다. 로그인이 필요합니다."
            ));
        }

        try {
            Integer savedProjectId = projectService.createProject(dto, currentUserId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "projectId", savedProjectId,
                    "message", "프로젝트 모집 글이 성공적으로 등록되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * [U] 프로젝트 게시글 수정
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> updateProject(
            @PathVariable("projectId") Integer projectId,
            @RequestBody ProjectRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "수정 권한이 없습니다. 로그인이 필요합니다."
            ));
        }

        try {
            com.reverse.nsu.entity.Users user = usersRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

            Integer roleId = null;
            if (user.getRole() != null) {
                roleId = user.getRole().getRoleId();
            }

            projectService.updateProject(projectId, dto, currentUserId, roleId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "프로젝트 모집 글이 성공적으로 수정되었습니다."
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * [D] 프로젝트 게시글 삭제
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> deleteProject(
            @PathVariable("projectId") Integer projectId,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "삭제 권한이 없습니다. 로그인이 필요합니다."
            ));
        }

        try {
            com.reverse.nsu.entity.Users user = usersRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

            Integer roleId = null;
            if (user.getRole() != null) {
                roleId = user.getRole().getRoleId();
            }

            projectService.deleteProject(projectId, currentUserId, roleId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "프로젝트 모집 글이 성공적으로 삭제되었습니다."
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * 프로젝트 모집 지원서 제출 (날짜, 시간 필드 제거 완료 버전)
     */
    @PostMapping("/{projectId}/apply")
    public ResponseEntity<Map<String, Object>> applyProject(
            @PathVariable("projectId") Integer projectId,
            @RequestBody ProjectApplyRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "로그인이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인해 주세요."
            ));
        }

        // 오직 이메일만 필수 값으로 체킹
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "이메일은 필수 입력해야 합니다."
            ));
        }

        if (dto.isPrivacyAgreement() == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "개인정보 수집 및 이용에 동의해야 지원이 가능합니다."
            ));
        }

        try {
            projectService.applyProject(projectId, currentUserId, dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "지원서가 성공적으로 접수되었습니다."
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}