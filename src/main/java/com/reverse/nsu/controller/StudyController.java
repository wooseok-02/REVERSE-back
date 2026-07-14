package com.reverse.nsu.controller;

import com.reverse.nsu.dto.StudyApplicationResponseDto;
import com.reverse.nsu.dto.StudyApplyRequestDto;
import com.reverse.nsu.dto.StudyRequestDto;
import com.reverse.nsu.dto.StudyResponseDto;
import com.reverse.nsu.entity.StudyStatus;
import com.reverse.nsu.repository.UsersRepository;
import com.reverse.nsu.service.RoleCheckService;
import com.reverse.nsu.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final UsersRepository usersRepository;
    private final RoleCheckService roleCheckService;

    private Integer resolveRoleId(String userId) {
        if (userId == null) return null;
        return usersRepository.findById(userId)
                .map(u -> u.getRole() != null ? u.getRole().getRoleId() : null)
                .orElse(null);
    }

    // 목록 조회 (검색 & 상태 필터 포함, 비로그인 가능)
    @GetMapping
    public ResponseEntity<Page<StudyResponseDto>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StudyStatus status,
            @PageableDefault(size = 6) Pageable pageable) {

        return ResponseEntity.ok(studyService.getStudyList(keyword, status, pageable));
    }

    // 단건 상세 조회 (비로그인 가능)
    @GetMapping("/{studyId}")
    public ResponseEntity<?> getOne(@PathVariable Integer studyId) {
        try {
            return ResponseEntity.ok(studyService.getStudyDetail(studyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 스터디 생성 (로그인 필수)
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody StudyRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            Integer studyId = studyService.createStudy(dto, currentUserId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "studyId", studyId,
                    "message", "스터디가 성공적으로 등록되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 스터디 신청 (정회원 이상: 정회원, 관리자, 최고관리자) - 가능 요일/시간 선택, 승인 대기 상태로 등록
    @PostMapping("/{studyId}/apply")
    public ResponseEntity<?> apply(
            @PathVariable Integer studyId,
            @RequestBody StudyApplyRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }
        if (!roleCheckService.isMemberOrAbove(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "정회원 이상만 스터디를 신청할 수 있습니다."));
        }

        try {
            studyService.applyStudy(studyId, currentUserId, dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "스터디 신청이 완료되었습니다. 승인을 기다려주세요."
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 스터디 신청 목록 조회 (팀장 또는 관리자/최고관리자) - 대기 중인 신청만
    @GetMapping("/{studyId}/applications")
    public ResponseEntity<?> getApplications(
            @PathVariable Integer studyId,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            List<StudyApplicationResponseDto> result =
                    studyService.getApplications(studyId, currentUserId, resolveRoleId(currentUserId));
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 스터디 신청 승인 (팀장 또는 관리자/최고관리자)
    @PatchMapping("/applications/{applicationId}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable Integer applicationId,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            studyService.approveApplication(applicationId, currentUserId, resolveRoleId(currentUserId));
            return ResponseEntity.ok(Map.of("success", true, "message", "신청이 승인되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 스터디 신청 반려 (팀장 또는 관리자/최고관리자)
    @PatchMapping("/applications/{applicationId}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable Integer applicationId,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            studyService.rejectApplication(applicationId, currentUserId, resolveRoleId(currentUserId));
            return ResponseEntity.ok(Map.of("success", true, "message", "신청이 반려되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 스터디 수정 (팀장만 가능)
    @PutMapping("/{studyId}")
    public ResponseEntity<?> update(
            @PathVariable Integer studyId,
            @RequestBody StudyRequestDto dto,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            StudyResponseDto response = studyService.updateStudy(studyId, dto, currentUserId, resolveRoleId(currentUserId));
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", response,
                    "message", "스터디가 수정되었습니다."
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 스터디 삭제 (팀장만 가능)
    @DeleteMapping("/{studyId}")
    public ResponseEntity<?> delete(
            @PathVariable Integer studyId,
            @RequestAttribute(value = "userId", required = false) String currentUserId) {

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            studyService.deleteStudy(studyId, currentUserId, resolveRoleId(currentUserId));
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "스터디가 삭제되었습니다."
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
