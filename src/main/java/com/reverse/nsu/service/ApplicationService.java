package com.reverse.nsu.service;

import com.reverse.nsu.dto.ApplicationRequestDto;
import com.reverse.nsu.entity.Recruitment;
import com.reverse.nsu.entity.RecruitmentApplication;
import com.reverse.nsu.repository.RecruitmentApplicationRepository;
import com.reverse.nsu.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;

    /**
     * 지원서 접수
     */
    @Transactional
    public void submitApplication(ApplicationRequestDto dto) {
        log.info(">>>> [지원서 접수 시작] 학번: {}, 성함: {}", dto.getStudentNumber(), dto.getUserName());

        // 1. 해당 공고 존재 여부 확인
        Recruitment recruit = recruitmentRepository.findById(dto.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모집 공고입니다."));

        // 2. 중복 지원 체크 (학번 기준)
        boolean isAlreadyApplied = applicationRepository.existsByRecruitment_RecruitmentIdAndStudentNumber(
                dto.getRecruitmentId(), dto.getStudentNumber());

        if (isAlreadyApplied) {
            throw new IllegalStateException("이미 해당 공고에 지원한 학번입니다.");
        }

        // 3. 엔티티 변환 및 저장
        RecruitmentApplication application = RecruitmentApplication.builder()
                .recruitment(recruit) // 연관 관계 매핑
                .userName(dto.getUserName())
                .userMajor(dto.getUserMajor())
                .studentNumber(dto.getStudentNumber())
                .userPhone(dto.getUserPhone())
                .grade(dto.getGrade())
                .userEmail(dto.getUserEmail())
                .portfolioUrl(dto.getPortfolioUrl())
                .termsAgreed(dto.getTermsAgreed())
                .status("PENDING") // 초기 상태 설정
                .build();

        applicationRepository.save(application);
        log.info(">>>> [지원서 접수 완료] 성함: {} 님", dto.getUserName());
    }

    // 추가 기능: 지원서 상태 변경 (합격/불합격 등)
    @Transactional
    public void updateApplicationStatus(Integer applicationId, String newStatus) {
        RecruitmentApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));
        app.setStatus(newStatus);
    }
}