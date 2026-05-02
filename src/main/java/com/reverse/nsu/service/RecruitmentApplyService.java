package com.reverse.nsu.service;

import com.reverse.nsu.dto.RecruitmentRequestDto;
import com.reverse.nsu.entity.*;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentApplyService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentInterviewSlotRepository slotRepository;

    @Transactional
    public void submitApplication(RecruitmentRequestDto dto) {
        log.info(">>>> [지원서 접수] 학번: {}, 성함: {}", dto.getStudentNumber(), dto.getUserName());

        // 1. 공고 객체 조회
        Recruitment recruit = recruitmentRepository.findById(dto.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));

        // 2. 중복 지원 체크 (선택 사항이지만 권장)
        if (applicationRepository.existsByRecruitment_RecruitmentIdAndStudentNumber(dto.getRecruitmentId(), dto.getStudentNumber())) {
            throw new IllegalStateException("이미 지원한 학번입니다.");
        }

        // 3. 지원서 엔티티 생성 (Builder 사용 권장)
        RecruitmentApplication app = RecruitmentApplication.builder()
                .recruitment(recruit)
                .userName(dto.getUserName())
                .userMajor(dto.getUserMajor())
                .studentNumber(dto.getStudentNumber()) // 학번 필드 추가됨
                .userPhone(dto.getUserPhone())
                .userEmail(dto.getUserEmail())
                .grade(dto.getGrade()) // 학년 필드 추가됨
                .portfolioUrl(dto.getPortfolioUrl())
                .termsAgreed(dto.getTermsAgreed())
                .status("PENDING")
                .build();

        // 4. 선택한 면접 슬롯(ID 리스트)을 스케줄 엔티티로 변환하여 연결
        if (dto.getSelectedSlotIds() != null) {
            dto.getSelectedSlotIds().forEach(slotId -> {
                RecruitmentInterviewSlot slot = slotRepository.findById(slotId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 면접 슬롯입니다. ID: " + slotId));

                // 중간 테이블 엔티티 생성
                ApplicationInterviewSchedule schedule = ApplicationInterviewSchedule.builder()
                        .application(app)
                        .interviewSlot(slot)
                        .build();

                // RecruitmentApplication 내의 리스트에 추가
                app.getInterviewSchedules().add(schedule);
            });
        }

        // 5. 최종 저장 (Cascade 설정에 의해 schedule도 함께 저장됨)
        applicationRepository.save(app);
        log.info(">>>> [접수 완료] {} 님의 지원서가 저장되었습니다.", dto.getUserName());
    }
}