package com.reverse.nsu.controller;

import com.reverse.nsu.entity.Recruitment;
import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.service.RecruitmentAdminService;
import com.reverse.nsu.service.RecruitmentNotifyService;
import com.reverse.nsu.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruit/admin")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RecruitmentAdminController {

    private final RecruitmentService recruitmentService;
    private final RecruitmentAdminService recruitmentAdminService;
    private final RecruitmentNotifyService notifyService;

    /**
     * 1. 특정 공고의 지원자 목록 조회
     */
    @GetMapping("/applications")
    public ResponseEntity<List<RecruitmentResponseDto.ApplicationDetails>> getApplications(
            @RequestParam Integer recruitmentId,
            @RequestParam Integer roleId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {

        recruitmentAdminService.validateAdminRole(roleId);
        return ResponseEntity.ok(recruitmentAdminService.getApplicationsByRecruit(recruitmentId, name, status));
    }

    /**
     * 2. 지원서 상태 변경
     */
    @PatchMapping("/applications/status")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, Object> request) {
        Integer roleId = (Integer) request.get("roleId");
        recruitmentAdminService.validateAdminRole(roleId);

        Integer applicationId = (Integer) request.get("applicationId");
        String status = (String) request.get("status");

        recruitmentAdminService.updateApplicationStatus(applicationId, status);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "지원서 상태가 " + status + "(으)로 변경되었습니다."
        ));
    }

    /**
     * 3. 공고 생성 (상세 페이지 자동 생성 포함)
     * 서비스 계층에서 Recruitment와 RecruitmentPage를 한 번에 생성합니다.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> request) {
        // 1. 권한 체크
        Integer roleId = (Integer) request.get("roleId");
        recruitmentAdminService.validateAdminRole(roleId);

        // 2. 서비스 호출 (공고 + 상세페이지 동시 생성)
        Recruitment recruitment = recruitmentAdminService.createRecruitment(request);

        // 3. 알림 서비스 호출
        notifyService.notifySubscribers(recruitment.getTitle());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "공고 및 상세 페이지 등록 완료",
                "recruitmentId", recruitment.getRecruitmentId(),
                "pageId", recruitment.getRecruitmentPage().getPageId()
        ));
    }

    /**
     * 4. 공고 내용 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request) {

        // 1. 권한 체크
        Integer roleId = (Integer) request.get("roleId");
        recruitmentAdminService.validateAdminRole(roleId);

        // 2. 서비스 호출 (수정 로직 위임)
        recruitmentAdminService.updateRecruitment(id, request);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "공고 수정 완료",
                "recruitmentId", id
        ));
    }

    /**
     * 5. 공고 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id, @RequestParam Integer roleId) {
        recruitmentAdminService.validateAdminRole(roleId);
        recruitmentService.delete(id);
        return ResponseEntity.ok("공고 삭제 완료. ID: " + id);
    }

    /**
     * 6. 지원서 상세 조회
     */
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<RecruitmentResponseDto.ApplicationDetails> getApplicationDetail(
            @PathVariable Integer applicationId,
            @RequestParam Integer roleId) {

        recruitmentAdminService.validateAdminRole(roleId);
        return ResponseEntity.ok(recruitmentAdminService.getApplicationDetail(applicationId));
    }

    /**
     * 7. 면접 스케줄 배정
     */
    @PostMapping("/applications/interview")
    public ResponseEntity<?> setInterview(@RequestBody Map<String, Object> request) {
        Integer roleId = (Integer) request.get("roleId");
        recruitmentAdminService.validateAdminRole(roleId);

        // 필요 시 recruitmentAdminService.setInterviewSchedule() 호출 로직 추가 가능

        return ResponseEntity.ok(Map.of("status", "success", "message", "면접 배정 완료"));
    }

    /**
     * 8. 엑셀 다운로드
     */
    @GetMapping("/applications/excel")
    public ResponseEntity<InputStreamResource> downloadExcel(
            @RequestParam Integer recruitmentId,
            @RequestParam Integer roleId) throws IOException {

        recruitmentAdminService.validateAdminRole(roleId);
        ByteArrayInputStream in = recruitmentAdminService.downloadApplicationsExcel(recruitmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=applications_recruit_" + recruitmentId + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}