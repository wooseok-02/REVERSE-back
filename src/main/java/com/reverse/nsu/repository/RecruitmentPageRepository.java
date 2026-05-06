package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecruitmentPageRepository extends JpaRepository<RecruitmentPage, Integer> {
    // 공고 ID로 상세 페이지 조회 (엔티티의 연관 관계 필드명 반영)
    Optional<RecruitmentPage> findByRecruitment_RecruitmentId(Integer recruitmentId);
}