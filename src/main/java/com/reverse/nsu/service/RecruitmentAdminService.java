package com.reverse.nsu.service;

import com.reverse.nsu.dto.ApplicationInterviewScheduleRequestDto;
import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.entity.ApplicationInterviewSchedule;
import com.reverse.nsu.entity.RecruitmentApplication;
import com.reverse.nsu.entity.RecruitmentInterviewSlot;
import com.reverse.nsu.repository.RecruitmentApplicationRepository;
import com.reverse.nsu.repository.RecruitmentInterviewSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentAdminService {

    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentInterviewSlotRepository interviewSlotRepository;

    /**
     * 관리자 권한 검증 (1: SUPER_ADMIN, 2: ADMIN)
     */
    public void validateAdminRole(Integer roleId) {
        if (roleId == null || roleId > 2) {
            throw new RuntimeException("접근 권한이 없습니다. 관리자 권한이 필요합니다.");
        }
    }

    /**
     * 1. 특정 공고의 지원자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<RecruitmentResponseDto.ApplicationDetails> getApplicationsByRecruit(
            Integer recruitmentId, String name, String status) {

        List<RecruitmentApplication> applications = applicationRepository.findAllByRecruitment_RecruitmentId(recruitmentId);

        return applications.stream()
                .filter(app -> name == null || name.trim().isEmpty() || app.getApplicantName().contains(name))
                .filter(app -> status == null || status.trim().isEmpty() || app.getStatus().equals(status))
                .map(this::convertToApplicationDetails)
                .collect(Collectors.toList());
    }

    /**
     * 2. 지원서 상태 변경 (합격/불합격 처리)
     */
    @Transactional
    public void updateApplicationStatus(Integer applicationId, String newStatus) {
        RecruitmentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지원서를 찾을 수 없습니다. ID: " + applicationId));

        application.setStatus(newStatus);
    }

    /**
     * 3. 지원서 상세 조회
     */
    @Transactional(readOnly = true)
    public RecruitmentResponseDto.ApplicationDetails getApplicationDetail(Integer applicationId) {
        RecruitmentApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지원서를 찾을 수 없습니다. ID: " + applicationId));

        return convertToApplicationDetails(app);
    }

    /**
     * 4. 면접 스케줄 설정
     */
    @Transactional
    public void setInterviewSchedule(ApplicationInterviewScheduleRequestDto dto) {
        RecruitmentApplication application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지원서를 찾을 수 없습니다. ID: " + dto.getApplicationId()));

        RecruitmentInterviewSlot slot = interviewSlotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("해당 면접 슬롯을 찾을 수 없습니다. ID: " + dto.getSlotId()));

        if (!application.getApplicationInterviewSchedule().isEmpty()) {
            application.getApplicationInterviewSchedule().clear();
        }

        ApplicationInterviewSchedule schedule = ApplicationInterviewSchedule.builder()
                .application(application)
                .interviewSlot(slot)
                .build();

        application.getApplicationInterviewSchedule().add(schedule);
    }

    /**
     * 5. 지원자 목록 엑셀 추출
     */
    @Transactional(readOnly = true)
    public ByteArrayInputStream downloadApplicationsExcel(Integer recruitmentId) throws IOException {
        List<RecruitmentApplication> applications = applicationRepository.findAllByRecruitment_RecruitmentId(recruitmentId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("지원자 목록");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"번호", "이름", "학번", "학과", "학년", "상태", "지원일시"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (RecruitmentApplication app : applications) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(app.getApplicationId());
                row.createCell(1).setCellValue(app.getApplicantName());
                row.createCell(2).setCellValue(app.getStudentNumber());
                row.createCell(3).setCellValue(app.getDepartment());
                row.createCell(4).setCellValue(app.getGrade());
                row.createCell(5).setCellValue(app.getStatus());
                // [수정] BaseTimeEntity의 getCreatedAt() 호출
                row.createCell(6).setCellValue(app.getCreatedAt() != null ? app.getCreatedAt().toString() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    /**
     * [헬퍼] 엔티티를 DTO 내부 클래스로 변환
     */
    private RecruitmentResponseDto.ApplicationDetails convertToApplicationDetails(RecruitmentApplication application) {
        return RecruitmentResponseDto.ApplicationDetails.builder()
                .applicationId(application.getApplicationId())
                .recruitmentId(application.getRecruitment().getRecruitmentId())
                .applicantName(application.getApplicantName())
                .department(application.getDepartment())
                .studentNumber(application.getStudentNumber())
                .phoneNumber(application.getPhoneNumber())
                .grade(application.getGrade())
                .email(application.getEmail())
                .termsAgreed(application.getTermsAgreed())
                .status(application.getStatus())
                // [수정] BaseTimeEntity 필드명(createdAt, updatedAt) 호출
                .createdDate(application.getCreatedAt())
                .modifiedDate(application.getUpdatedAt())
                .build();
    }
}