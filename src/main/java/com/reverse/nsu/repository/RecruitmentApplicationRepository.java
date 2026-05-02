package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface RecruitmentApplicationRepository extends JpaRepository<RecruitmentApplication, Integer> {

    // 1. 개인정보 이용에 동의한 지원자 목록 조회
    List<RecruitmentApplication> findAllByTermsAgreedTrue();

    // 2. 특정 모집 공고의 모든 지원서 조회
    List<RecruitmentApplication> findAllByRecruitment_RecruitmentId(Integer recruitmentId);

    // 3. 특정 모집 공고 내 학번으로 중복 지원 여부 확인
    boolean existsByRecruitment_RecruitmentIdAndStudentNumber(Integer recruitmentId, String studentNumber);

    // 4. 특정 모집 공고 내 상태별 지원자 목록 조회 (예: 합격자만 조회)
    List<RecruitmentApplication> findAllByRecruitment_RecruitmentIdAndStatus(Integer recruitmentId, String status);

    @Modifying
    @Transactional
    void deleteByRecruitment_RecruitmentId(Integer recruitmentId);
}