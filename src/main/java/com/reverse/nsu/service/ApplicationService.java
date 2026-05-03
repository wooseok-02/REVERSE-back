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

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private final RecruitmentApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    public void submitApplication(ApplicationRequestDto dto) {
        // 1. 공고 존재 및 기간 확인 (정의서: "지금은 신청 기간이 아닙니다")
        Recruitment recruit = recruitmentRepository.findById(dto.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모집 공고입니다."));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(recruit.getApplyStartDate()) || now.isAfter(recruit.getApplyEndDate())) {
            throw new IllegalStateException("지금은 신청 기간이 아닙니다.");
        }

        // 2. 필수값 체크 (정의서: "모든 입력 not Null")
        validateRequiredFields(dto);

        // 3. 개인정보 동의 체크 (정의서: "개인정보 수집 미 동의 제출 불가")
        if (dto.getTermsAgreed() == null || !dto.getTermsAgreed()) {
            throw new IllegalArgumentException("개인정보 수집에 동의해야 제출이 가능합니다.");
        }

        // 4. 학번 중복 체크 (정의서: "이미 신청하셨습니다.")
        if (applicationRepository.existsByRecruitment_RecruitmentIdAndStudentNumber(
                dto.getRecruitmentId(), dto.getStudentNumber())) {
            throw new IllegalStateException("이미 신청하셨습니다.");
        }

        // 5. 전화번호 형식 검증 (정의서 문구 반영)
        // [수정] userPhone -> phoneNumber
        if (dto.getPhoneNumber() == null || !dto.getPhoneNumber().matches("^\\d{3}-\\d{3,4}-\\d{4}$")) {
            throw new IllegalArgumentException("휴대폰 번호는 000-0000-0000 형식으로 입력해주세요.");
        }

        // 6. 이메일 도메인 검증 (정의서 문구 반영)
        // [수정] userEmail -> email
        if (dto.getEmail() == null || !dto.getEmail().contains("@") || dto.getEmail().endsWith("@")) {
            throw new IllegalArgumentException("그런 도메인 없다 돌아가라.");
        }

        // 7. 저장
        // DTO 내부의 toEntity를 호출 (이미 내부에서 필드 매핑 완료됨)
        RecruitmentApplication application = dto.toEntity(recruit);
        applicationRepository.save(application);

        log.info(">>>> [지원서 접수 완료] 학번: {}", dto.getStudentNumber());
    }

    /**
     * 필수 필드 검증 메서드
     */
    private void validateRequiredFields(ApplicationRequestDto dto) {
        // [수정] 변경된 DTO 필드명(applicantName, phoneNumber, email)에 맞춰 검증
        if (dto.getApplicantName() == null || dto.getStudentNumber() == null ||
                dto.getPhoneNumber() == null || dto.getEmail() == null || dto.getDepartment() == null) {
            throw new IllegalArgumentException("모든 필수 항목을 입력해주세요.");
        }
    }
}