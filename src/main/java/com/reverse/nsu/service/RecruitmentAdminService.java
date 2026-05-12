package com.reverse.nsu.service;

import com.reverse.nsu.dto.ApplicationInterviewScheduleRequestDto;
import com.reverse.nsu.dto.RecruitmentRequestDto;
import com.reverse.nsu.dto.RecruitmentResponseDto;
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.RecruitmentApplicationRepository;
import com.reverse.nsu.repository.RecruitmentInterviewSlotRepository;
import com.reverse.nsu.repository.RecruitmentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentAdminService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentInterviewSlotRepository interviewSlotRepository;
    private final EntityManager entityManager;

    /**
     * 관리자 권한 검증 (1: SUPER_ADMIN, 2: ADMIN)
     */
    public void validateAdminRole(Integer roleId) {
        if (roleId == null || roleId > 2) {
            throw new RuntimeException("접근 권한이 없습니다. 관리자 권한이 필요합니다.");
        }
    }

    /**
     * 공고 및 상세 페이지 동시 생성
     */
    @Transactional
    public Recruitment createRecruitment(Map<String, Object> request) {
        String adminId = (String) request.get("adminId");

        RecruitmentPage page = RecruitmentPage.builder()
                .heroTitle((String) request.get("title"))
                .heroSubTitle("동아리 상세 정보")
                .heroBtnText("지원하기")
                .heroYear("2026")
                .heroBgUrl("")
                .updatedBy(adminId)
                .isActive(true)
                .build();

        Recruitment recruitment = Recruitment.builder()
                .title((String) request.get("title"))
                .description((String) request.get("description"))
                .applyStartDate(LocalDateTime.parse((String) request.get("applyStartDate")))
                .applyEndDate(LocalDateTime.parse((String) request.get("applyEndDate")))
                .updatedBy(adminId)
                .isActive(true)
                .build();

        recruitment.setRecruitmentPage(page);
        page.setRecruitment(recruitment);

        return recruitmentRepository.save(recruitment);
    }

    /**
     * 공고 기본 정보 수정 로직
     */
    @Transactional
    public void updateRecruitment(Integer id, Map<String, Object> request) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공고가 존재하지 않습니다. ID: " + id));

        recruitment.update(
                (String) request.get("title"),
                (String) request.get("description"),
                LocalDateTime.parse((String) request.get("applyStartDate")),
                LocalDateTime.parse((String) request.get("applyEndDate")),
                (String) request.get("adminId")
        );
    }

    /**
     * 상세 페이지 통합 수정 로직 (Hero, Intro, Card, Gallery, Contact)
     */
    @Transactional
    public void updateRecruitmentPage(Integer recruitmentId, RecruitmentRequestDto.PageUpdate dto) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("공고를 찾을 수 없습니다. ID: " + recruitmentId));

        RecruitmentPage page = recruitment.getRecruitmentPage();
        String adminId = dto.getAdminId();

        // 1. Hero 섹션 업데이트
        page.setHeroYear(dto.getHeroYear());
        page.setHeroTitle(dto.getHeroTitle());
        page.setHeroSubTitle(dto.getHeroSubTitle());
        page.setHeroBtnText(dto.getHeroBtnText());
        page.setHeroBgUrl(dto.getHeroBgUrl());
        page.setUpdatedBy(adminId);

        // 2. 기존 리스트 비우기 및 즉시 반영 (중복 키 방지)
        page.getIntros().clear();
        page.getCards().clear();
        page.getGalleries().clear();
        page.getContacts().clear();
        entityManager.flush();

        // 3. 새 데이터 추가
        if (dto.getIntros() != null) {
            dto.getIntros().forEach(i -> page.getIntros().add(
                    RecruitmentPageIntro.builder().recruitmentPage(page)
                            .contents(i.getContents()).sortOrder(i.getSortOrder())
                            .updatedBy(adminId).build()));
        }

        if (dto.getCards() != null) {
            dto.getCards().forEach(c -> page.getCards().add(
                    RecruitmentPageFieldCard.builder().recruitmentPage(page)
                            .applyField(c.getApplyField()).cardTitle(c.getCardTitle())
                            .cardSubTitle(c.getCardSubTitle()).cardDesc(c.getCardDesc())
                            .imageUrl(c.getImageUrl()).sortOrder(c.getSortOrder())
                            .updatedBy(adminId).build()));
        }

        if (dto.getGalleries() != null) {
            dto.getGalleries().forEach(g -> page.getGalleries().add(
                    RecruitmentPageGallery.builder().recruitmentPage(page)
                            .imageUrl(g.getImageUrl()).imageDesc(g.getImageDesc())
                            .tag(g.getTag()).sortOrder(g.getSortOrder())
                            .updatedBy(adminId).build()));
        }

        if (dto.getContacts() != null) {
            dto.getContacts().forEach(con -> page.getContacts().add(
                    RecruitmentPageContact.builder().recruitmentPage(page)
                            .contactType(con.getContactType()).label(con.getLabel())
                            .value(con.getValue()).subValue(con.getSubValue())
                            .sortOrder(con.getSortOrder()).updatedBy(adminId).build()));
        }
    }

    /**
     * 면접 일정 슬롯 설정 로직 (기존 슬롯 초기화 후 재생성)
     */
    @Transactional
    public void updateInterviewSlots(Integer recruitmentId, RecruitmentRequestDto.InterviewSlotUpdate dto) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("공고를 찾을 수 없습니다."));

        // 1. 기존 슬롯 삭제
        interviewSlotRepository.deleteByRecruitment_RecruitmentId(recruitmentId);
        entityManager.flush();

        // 2. 새로운 슬롯 등록
        if (dto.getSlots() != null) {
            dto.getSlots().forEach(s -> {
                RecruitmentInterviewSlot slot = RecruitmentInterviewSlot.builder()
                        .recruitment(recruitment)
                        .slotDate(s.getSlotDate())
                        .capacity(s.getCapacity())
                        .isActive(true)
                        .updatedBy(dto.getAdminId())
                        .build();
                interviewSlotRepository.save(slot);
            });
        }
    }

    /**
     * 특정 공고의 지원자 목록 조회
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
     * 지원서 상태 변경
     */
    @Transactional
    public void updateApplicationStatus(Integer applicationId, String newStatus) {
        RecruitmentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지원서를 찾을 수 없습니다. ID: " + applicationId));

        application.setStatus(newStatus);
    }

    /**
     * 지원서 상세 조회
     */
    @Transactional(readOnly = true)
    public RecruitmentResponseDto.ApplicationDetails getApplicationDetail(Integer applicationId) {
        RecruitmentApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지원서를 찾을 수 없습니다. ID: " + applicationId));

        return convertToApplicationDetails(app);
    }

    /**
     * 개별 지원자 면접 스케줄 설정
     */
    @Transactional
    public void setInterviewSchedule(ApplicationInterviewScheduleRequestDto dto) {
        RecruitmentApplication application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지원서를 찾을 수 없습니다."));

        RecruitmentInterviewSlot slot = interviewSlotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("해당 면접 슬롯을 찾을 수 없습니다."));

        application.getApplicationInterviewSchedule().clear();

        ApplicationInterviewSchedule schedule = ApplicationInterviewSchedule.builder()
                .application(application)
                .interviewSlot(slot)
                .build();

        application.getApplicationInterviewSchedule().add(schedule);
    }

    /**
     * 지원자 목록 엑셀 추출
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
                row.createCell(6).setCellValue(app.getCreatedAt() != null ? app.getCreatedAt().toString() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

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
                .createdDate(application.getCreatedAt())
                .modifiedDate(application.getUpdatedAt())
                .build();
    }
}