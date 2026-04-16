package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


public interface RecruitmentApplicationRepository extends JpaRepository<RecruitmentApplication, Integer> {
    // 개인정보 이용에 동의한 지원자 목록 조회
    List<RecruitmentApplication> findAllByTermsAgreedTrue();

    @Modifying
    @Transactional
    void deleteByRecruitmentId(Integer recruitmentId);
}