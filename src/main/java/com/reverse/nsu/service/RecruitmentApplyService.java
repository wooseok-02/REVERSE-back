package com.reverse.nsu.service;

import com.reverse.nsu.dto.ApplicationRequestDto; // DTO 클래스명 확인
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentApplyService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentInterviewSlotRepository slotRepository;

    @Transactional
    public void submitApplication(ApplicationRequestDto dto) {
        // [수정] DTO 필드명 변경: studentId -> studentNumber, studentName -> applicantName
        log.info(">>>> [지원서 접수 시작] 학번: {}, 성함: {}", dto.getStudentNumber(), dto.getApplicantName());

        // 1. 공고 객체 조회 및 지원 기간 검증
        Recruitment recruit = recruitmentRepository.findById(dto.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다. ID: " + dto.getRecruitmentId()));

        validateApplicationPeriod(recruit);

        // 2. 중복 지원 체크
        if (applicationRepository.existsByRecruitment_RecruitmentIdAndStudentNumber(dto.getRecruitmentId(), dto.getStudentNumber())) {
            log.warn(">>>> [접수 실패] 이미 지원한 학번입니다. 학번: {}", dto.getStudentNumber());
            throw new IllegalStateException("이미 신청하셨습니다.");
        }

        // 3. 개인정보 수집 동의 체크
        if (dto.getTermsAgreed() == null || !dto.getTermsAgreed()) {
            throw new IllegalArgumentException("개인정보 수집 및 이용에 동의해야 제출이 가능합니다.");
        }

        // 4. 지원서 엔티티 생성
        // [수정 포인트] 엔티티 필드명 및 DTO getter 명칭 통일, portfolioUrl 제거
        RecruitmentApplication app = RecruitmentApplication.builder()
                .recruitment(recruit)
                .applicantName(dto.getApplicantName()) // userName -> applicantName
                .department(dto.getDepartment())       // userMajor -> department
                .studentNumber(dto.getStudentNumber()) // studentId -> studentNumber
                .phoneNumber(dto.getPhoneNumber())     // userPhone -> phoneNumber
                .email(dto.getEmail())                 // userEmail -> email
                .grade(dto.getGrade())
                // .portfolioUrl(dto.getPortfolioUrl()) // [방법 1] DB에 없으므로 삭제
                .termsAgreed(dto.getTermsAgreed() ? 1 : 0) // Boolean -> Integer 변환 (필요시)
                .status("PENDING")
                .build();

        // 5. 면접 슬롯 연결 및 유효성 검사 (정의서: 면접 기간 외 신청 불가)
        // 주의: ApplicationRequestDto에 interviewSlotId 필드가 있는지 확인 필요
        /*
        if (dto.getInterviewSlotId() != null) {
            Integer slotId = dto.getInterviewSlotId();
            RecruitmentInterviewSlot slot = slotRepository.findById(slotId)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 면접 슬롯입니다."));

            if (!slot.getIsActive()) {
                throw new IllegalStateException("해당 날짜는 선택할 수 없습니다.");
            }

            ApplicationInterviewSchedule schedule = ApplicationInterviewSchedule.builder()
                    .application(app)
                    .interviewSlot(slot)
                    .build();

            app.getApplicationInterviewSchedule().add(schedule);
        }
        */

        // 6. 최종 저장
        try {
            applicationRepository.save(app);
            log.info(">>>> [접수 성공] {} 님의 지원서가 등록되었습니다.", dto.getApplicantName());
        } catch (Exception e) {
            log.error(">>>> [저장 오류] 지원서 저장 중 예외 발생: {}", e.getMessage());
            throw new RuntimeException("지원서 제출 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    /**
     * 화면정의서 [🚨 예외 처리] 문구 반영
     */
    private void validateApplicationPeriod(Recruitment recruit) {
        LocalDateTime now = LocalDateTime.now();
        if (recruit.getApplyStartDate() != null && now.isBefore(recruit.getApplyStartDate())) {
            throw new IllegalStateException("지금은 신청 기간이 아닙니다");
        }
        if (recruit.getApplyEndDate() != null && now.isAfter(recruit.getApplyEndDate())) {
            throw new IllegalStateException("지금은 신청 기간이 아닙니다");
        }
    }
}