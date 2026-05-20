package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ProjectApplyRequestDto;
import com.reverse.nsu.dto.ProjectRequestDto;
import com.reverse.nsu.dto.ProjectResponseDto;
import com.reverse.nsu.entity.ProjectStatus;
import com.reverse.nsu.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * ACT01-PRT03: 프로젝트 게시글 목록 확인 (검색 & 필터링 포함)
     * - 비로그인 유저도 접근 가능
     */
    @GetMapping
    public ResponseEntity<Page<ProjectResponseDto>> getProjectList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) ProjectStatus status,
            @PageableDefault(size = 6) Pageable pageable) {

        Page<ProjectResponseDto> projects = projectService.getProjectList(keyword, status, pageable);
        return ResponseEntity.ok(projects);
    }

    /**
     * 특정 프로젝트 상세 정보 조회 (팝업창 노출용)
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProjectDetail(@PathVariable("projectId") Integer projectId) {
        ProjectResponseDto project = projectService.getProjectDetail(projectId);
        return ResponseEntity.ok(project);
    }

    /**
     * 프로젝트 게시글 작성
     * - [구조 통일] @RequestAttribute를 사용하여 토큰에서 파싱된 userId를 바인딩합니다.
     * - [응답 추가] 프론트엔드 라우팅 편의성을 위해 성공 시 생성된 projectId를 함께 반환합니다.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProject(
            @RequestBody ProjectRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        // 1. 토큰 입구 컷 검사 (인증 실패)
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "프로젝트 게시글 작성 권한이 없습니다. 로그인이 필요합니다."
            ));
        }

        try {
            // 2. 서비스 레이어 호출 및 생성된 고유 ID 수신
            Integer savedProjectId = projectService.createProject(dto, currentUserId);

            // 🔥 [수정 포인트] 프론트엔드가 사용할 수 있도록 생성된 projectId를 함께 반환합니다.
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
     * ACT01-PRT01: 프로젝트 모집 지원서 제출
     * - Bearer 토큰만 헤더에 넣어주면 JwtFilter가 추출한 userId를 완벽하게 바인딩합니다.
     */
    @PostMapping("/{projectId}/apply")
    public ResponseEntity<Map<String, Object>> applyProject(
            @PathVariable("projectId") Integer projectId,
            @RequestBody ProjectApplyRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        // 1. 토큰이 없거나 유효하지 않아 userId 파싱이 실패한 경우 (인증 실패)
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "로그인이 만료되었거나 토큰이 유효하지 않습니다. 다시 로그인해 주세요."
            ));
        }

        // 2. 필수 입력값 누락 검증 (기획서 예외처리 우선순위 반영)
        if (dto.getEmail() == null || dto.getEmail().isBlank() ||
                dto.getAvailableDate() == null ||
                dto.getAvailableTime() == null || dto.getAvailableTime().isBlank()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "요일과 시간, 이메일은 필수 입력해야 합니다."
            ));
        }

        // 디버깅용 로그 확인
        System.out.println("====== [인증 완료] 현재 지원을 시도하는 유저 ID: [" + currentUserId + "] ======");

        try {
            // 3. 실제 DB 검증 및 로직 수행 (중복 지원 검증 포함)
            projectService.applyProject(projectId, currentUserId, dto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "지원서가 성공적으로 접수되었습니다 🙌"
            ));

        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}