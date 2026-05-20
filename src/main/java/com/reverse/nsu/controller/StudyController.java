package com.reverse.nsu.controller;

import com.reverse.nsu.dto.StudyRequestDto;
import com.reverse.nsu.dto.StudyResponseDto;
import com.reverse.nsu.entity.StudyStatus;
import com.reverse.nsu.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

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
            StudyResponseDto response = studyService.updateStudy(studyId, dto, currentUserId);
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
            studyService.deleteStudy(studyId, currentUserId);
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
